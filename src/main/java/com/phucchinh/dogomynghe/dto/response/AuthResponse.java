package com.phucchinh.dogomynghe.dto.response;

import com.phucchinh.dogomynghe.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {

    // Token JWT được tạo ra sau khi đăng nhập thành công
    String accessToken;

    String refreshToken;

    Instant expiryAt;

    // Loại token, thường là "Bearer"
    @Builder.Default
    String tokenType = "Bearer";

    // Thông tin cơ bản của người dùng đã đăng nhập
    Long id;
    String username;
    UserRole userRole;
}
