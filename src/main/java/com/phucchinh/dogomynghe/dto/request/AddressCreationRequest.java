package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressCreationRequest {

    // Các trường địa chỉ phải khớp với Address.java của bạn

    @NotBlank(message = "Tỉnh/Thành phố không được để trống!")
    String city; // Thành phố

    @NotBlank(message = "Xã/Phường không được để trống!")
    String commune; // Xã, Phường

    @NotBlank(message = "Thôn/Tổ dân phố không được để trống!")
    String village; // Thôn, Tổ dân phố

    // Địa chỉ chi tiết (có thể không bắt buộc)
    String note; // Địa chỉ cụ thể: số nhà, tên đường
}