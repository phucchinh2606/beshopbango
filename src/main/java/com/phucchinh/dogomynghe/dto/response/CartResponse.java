package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    Long id; // ID của Cart
    Long userId;
    List<CartItemResponse> items;
    Long totalAmount; // Tổng tiền của tất cả các mặt hàng
}