package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long id;
    String name;
    String description;
    String imageUrl; // URL ảnh
    Long price;
    CategoryResponse category; // Trả về thông tin danh mục
    LocalDateTime createdAt;
}