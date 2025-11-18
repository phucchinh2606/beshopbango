package com.phucchinh.dogomynghe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    // Mối quan hệ 1-1 với User
    // User là bên sở hữu giỏ hàng, nên khóa ngoại sẽ nằm trong bảng 'carts'
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;

    // Mối quan hệ 1-n với CartItem
    // mappedBy trỏ đến trường "cart" trong lớp CartItem
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<CartItem> items;
}