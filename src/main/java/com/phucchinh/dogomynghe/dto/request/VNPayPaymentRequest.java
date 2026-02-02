package com.phucchinh.dogomynghe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayPaymentRequest {

    Long orderId;
    Long amount;
    String orderInfo;
    String bankCode; // Optional: mã ngân hàng (VCB, TCB, etc.)
}