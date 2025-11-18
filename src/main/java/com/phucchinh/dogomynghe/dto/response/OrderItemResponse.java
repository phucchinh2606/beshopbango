package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {

    Long id; // ID của OrderItem
    ProductMinimalResponse product; // Thông tin sản phẩm
    int quantity;
    Long priceAtPurchase; // Giá tại thời điểm mua (Rất quan trọng)
    Long subTotal; // (quantity * priceAtPurchase)
}