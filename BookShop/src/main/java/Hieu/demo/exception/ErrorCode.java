package Hieu.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY_ENUM(1001,"Invalid key Enum",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed!!",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must has at least 4 characters",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"Invalid Password",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1005, "User not found!!",HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1006, "User not exist!!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated!",HttpStatus.UNAUTHORIZED),
    INVALID_FORMART_TOKEN(1008,"Invalid format Token!!",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1009,"You do not have permission!",HttpStatus.FORBIDDEN)
    ;


    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
