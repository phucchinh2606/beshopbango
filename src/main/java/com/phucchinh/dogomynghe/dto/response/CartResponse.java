package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    Long id; // ID của Cart

    List<CartItemResponse> items;

    // SỬA: Đổi từ totalAmount thành totalCartPrice cho khớp Service
    Long totalCartPrice;

    LocalDateTime createdAt;
}