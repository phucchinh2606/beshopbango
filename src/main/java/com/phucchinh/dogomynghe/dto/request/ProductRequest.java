package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống!")
    String name;

    String description;

    @NotNull(message = "Giá sản phẩm không được để trống!")
    @Min(value = 0, message = "Giá không được nhỏ hơn 0.")
    Long price;

    @NotNull(message = "Danh mục sản phẩm không được để trống!")
    Long categoryId; // ID của danh mục
}