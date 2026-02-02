package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {

    Long id; // ID của CartItem

    // SỬA: Thay thế các trường rời (id, name, img, price) bằng Object này
    ProductMinimalResponse product;

    int quantity;

    // Giữ lại subtotal để Frontend đỡ phải tính
    Long subtotal;
}