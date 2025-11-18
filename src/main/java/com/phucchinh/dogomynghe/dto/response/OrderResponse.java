package com.phucchinh.dogomynghe.dto.response;

import com.phucchinh.dogomynghe.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    Long orderId;
    OrderStatus status;
    Long totalAmount;
    LocalDateTime createdAt;

    // Thông tin địa chỉ giao hàng
    AddressResponse shippingAddress;

    // Danh sách sản phẩm
    List<OrderItemResponse> items;

    // ⭐️ TRƯỜNG MỚI: Thông tin người đặt hàng
    UserMinimalResponse user;
}