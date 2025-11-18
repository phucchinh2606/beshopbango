package com.phucchinh.dogomynghe.dto.request;

import com.phucchinh.dogomynghe.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusUpdateRequest {

    @NotNull(message = "Trạng thái mới không được để trống")
    OrderStatus newStatus;
}