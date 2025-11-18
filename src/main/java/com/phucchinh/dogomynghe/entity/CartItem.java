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
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    int quantity; // Số lượng sản phẩm

    // Mối quan hệ N-1 với Product (Lưu ý: Không nên Cascade.ALL ở đây)
    // Giữ Product luôn là Entity độc lập.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    // Mối quan hệ N-1 với Cart (Đây là bên sở hữu khóa ngoại)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    Cart cart;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();
}