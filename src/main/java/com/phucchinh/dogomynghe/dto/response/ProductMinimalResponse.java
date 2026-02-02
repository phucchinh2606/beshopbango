package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductMinimalResponse {
    Long id;
    String name;
    String imageUrl;
    Long price; // Giá hiện tại (để tham khảo)
    Integer stockQuantity;
}