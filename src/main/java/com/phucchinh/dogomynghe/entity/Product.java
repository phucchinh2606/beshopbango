package com.phucchinh.dogomynghe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    @Column(columnDefinition = "TEXT")
    String description;
    String imageUrl;
    Long price;

    // 👇 1. THÊM TRƯỜNG TỒN KHO
    @Builder.Default // Giá trị mặc định khi build là 0
            Integer stockQuantity = 0;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
}
