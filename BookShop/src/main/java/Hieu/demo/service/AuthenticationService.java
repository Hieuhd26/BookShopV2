package Hieu.demo.service;

import Hieu.demo.dto.request.AuthenticationRequest;
import Hieu.demo.dto.request.IntrospectRequest;
import Hieu.demo.dto.request.LogoutRequest;
import Hieu.demo.dto.request.RefreshRequest;
import Hieu.demo.dto.response.AuthenticationResponse;
import Hieu.demo.dto.response.IntrospectResponse;
import Hieu.demo.entity.InvalidatedToken;
import Hieu.demo.entity.User;
import Hieu.demo.exception.AppException;
import Hieu.demo.exception.ErrorCode;
import Hieu.demo.repository.InvalidatedTokenRepository;
import Hieu.demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerkey}")
    protected String SIGNER_KEY; // sao lai bo static

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        //kiem tra dang nhap chua
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // tao token va tra
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token).authenticated(true).build();

    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        //doc thong tin token lay ra id va thoi diem het han (expery time)
        var signToken = verifyToken(request.getToken());
        String jid = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jid)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);


    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        // thu vien cung cap
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT;
        Date expiryTime;
        try {
            signedJWT = SignedJWT.parse(token);
            // kiem tra het thoi han chua
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_FORMART_TOKEN);
        }
        // kiem tra la co phai do he thong minh tao ra khong
        boolean verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        //kiem tra token da duoc luu vao bang het han trong data chua
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }


    public String generateToken(User user) {
        //2.header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        //3.tao claimSet
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("bookshop.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        //4.tao payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // 1.tao token thi can JWSObject (yeu cau header, payload)
        JWSObject jwsObject = new JWSObject(header, payload);

        //5.ki token
        // ki dung khoa trung nhau
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot gen token ", e);
            throw new RuntimeException(e);
        }
    }

    // valid token xem co phai cua minh khong
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid).build();
    }

    // xd scope trong claim gom cac role, theo dang string cho user
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }

            });
        }
        return stringJoiner.toString();
    }


    public AuthenticationResponse refreshToke(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken());

        var jid = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jid)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token).authenticated(true).build();

    }


}
