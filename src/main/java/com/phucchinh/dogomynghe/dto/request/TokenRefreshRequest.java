// src/main/java/com/phucchinh/dogomynghe/dto/request/TokenRefreshRequest.java
package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token không được để trống")
    String refreshToken;
}