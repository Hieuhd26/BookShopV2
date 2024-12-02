package Hieu.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL) // field nao null thi khong hien thi
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
     @Builder.Default
     int code = 1000;
     String message;
     T result;
}
