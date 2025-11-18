package com.phucchinh.dogomynghe.dto.response;

import com.phucchinh.dogomynghe.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long id;
    String username;
    String email;
    String phoneNumber;
    UserRole userRole;
    LocalDateTime createdAt;
}