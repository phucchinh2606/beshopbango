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
    Long productId;
    String productName;
    String productImageUrl;
    Long price; // Giá bán (đơn vị)
    int quantity;
    Long subtotal; // Thành tiền: price * quantity
}