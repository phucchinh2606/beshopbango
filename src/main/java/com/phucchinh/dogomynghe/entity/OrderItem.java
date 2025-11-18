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
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    int quantity; // Số lượng sản phẩm

    // RẤT QUAN TRỌNG: Giá sản phẩm tại thời điểm đặt hàng
    @Column(nullable = false)
    Long priceAtPurchase;

    // ⭐️ THÊM TRƯỜNG NÀY (Để lưu tên tại thời điểm mua)
    String productName;

    // Mối quan hệ N-1 với Order (Bên sở hữu khóa ngoại order_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    // Mối quan hệ N-1 với Product (Tham chiếu đến sản phẩm gốc)
    // Lưu ý: Không Cascade.ALL để không ảnh hưởng đến Product gốc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();
}