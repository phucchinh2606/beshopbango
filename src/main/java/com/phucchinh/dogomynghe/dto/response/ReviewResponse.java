package com.phucchinh.dogomynghe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Long id;
    String username; // Tên người đánh giá
    int rating;
    String comment;
    LocalDateTime createdAt;
    // Có thể thêm avatar user nếu muốn
}