package com.phucchinh.dogomynghe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Điểm đánh giá (thường từ 1 đến 5)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    int rating;

    // Nội dung đánh giá (có thể null nếu chỉ gửi rating)
    String comment;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    // Mối quan hệ N-1 với User (Bên sở hữu khóa ngoại user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Mối quan hệ N-1 với Product (Bên sở hữu khóa ngoại product_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
}
