package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {

    @NotNull(message = "ID Địa chỉ (addressId) là bắt buộc!")
    Long addressId;

    // ⭐️ THAY ĐỔI QUAN TRỌNG:
    // Client phải gửi lên danh sách ID của các CartItem muốn thanh toán.
    @NotEmpty(message = "Bạn phải chọn ít nhất một sản phẩm để thanh toán.")
    List<Long> cartItemIds;

    String customerNote; // Giữ lại ghi chú (Tôi sẽ thêm vào Order entity)
}