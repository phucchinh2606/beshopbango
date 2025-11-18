package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {

    // Các trường phải khớp với Address.java của bạn

    Long id;
    String city;
    String commune;
    String village;
    String note;
    LocalDateTime createdAt;

    // Bạn có thể thêm trường để hiển thị địa chỉ đầy đủ dễ đọc
    String fullAddress;
}