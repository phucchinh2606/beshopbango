package com.phucchinh.dogomynghe.dto.response;

import com.phucchinh.dogomynghe.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

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

    // 👇 BẮT BUỘC PHẢI CÓ CÁC TRƯỜNG NÀY 👇
    List<AddressResponse> addresses;
    CartResponse cart;
    List<OrderResponse> orders;
}