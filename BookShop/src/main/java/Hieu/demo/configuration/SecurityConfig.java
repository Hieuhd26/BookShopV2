package Hieu.demo.configuration;

import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private String[] PUBLIC_ENDPOINT = {"/users", "auth/token", "auth/introspect","/auth/logout"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                        //  .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated());
        //dk auth provider de sp cho jwt
        httpSecurity.oauth2ResourceServer( // auth cho jwt do chung ta gen, chung ta dang la resource server
                // neu ta cau hinh resource server thu 3 thi dung  jwkSetUri
                oauth2 -> oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(jwtDecoder())
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //diem ma authentication false thi dieu huong di dau hoac trong th nay tra ve error mess, tako dieu huong di dau ca
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        httpSecurity.csrf(config -> config.disable());
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Value("${jwt.signerkey}")
    private String signerKey;

    // implement
    @Bean
    public JwtDecoder jwtDecoder() {
        //2.
        SecretKeySpec spec = new SecretKeySpec(signerKey.getBytes(), "HS256");
        //1. ta dung secrete key
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(spec).macAlgorithm(MacAlgorithm.HS256).build();
        return nimbusJwtDecoder;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // chuyen tu scope sang role
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        //jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
