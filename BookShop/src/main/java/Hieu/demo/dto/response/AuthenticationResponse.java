package Hieu.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;
}
//1. Tao AuthenticationResponse
//2. kiem tra dang nhap hay chua
//3. tao token (generateToken)...
//4. chinh lai controller

// kiem tra token co phai do he thong minh tao ra hay khong
//1. tao introspect req, res
//2. tao introspect method ...
//3.
