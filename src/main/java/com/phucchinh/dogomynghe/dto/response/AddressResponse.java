package com.phucchinh.dogomynghe.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse implements Serializable {
/*
    // Các trường phải khớp với Address.java của bạn

    Long id;
    String city;
    String commune;
    String village;
    String note;
    LocalDateTime createdAt;

    // Bạn có thể thêm trường để hiển thị địa chỉ đầy đủ dễ đọc
    String fullAddress;

 */
    @NotNull
    private String type;

    @NotNull
    private String address;

    @NotNull
    private int wardId;

    @NotNull
    private String wardName;

    @NotNull
    private int provinceId;

    @NotNull
    private String provinceName;
}