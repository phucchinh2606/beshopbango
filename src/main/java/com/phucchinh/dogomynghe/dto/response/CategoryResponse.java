package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {

    Long id;
    String name;
    String imageUrl;
    LocalDateTime createdAt;
}