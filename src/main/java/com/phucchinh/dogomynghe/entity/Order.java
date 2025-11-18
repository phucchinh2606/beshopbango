package com.phucchinh.dogomynghe.entity;

import com.phucchinh.dogomynghe.enums.OrderStatus; // Giả định bạn có Enum này
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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Trạng thái đơn hàng (CART, PLACED, SHIPPED, DELIVERED, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status = OrderStatus.PENDING; // Mặc định là đã đặt (PLACED)

    // Tổng tiền (có thể tính toán từ OrderItems)
    Long totalAmount;

    // Mối quan hệ N-1 với User (Người dùng đặt đơn hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Mối quan hệ 1-N với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<OrderItem> items;

    // ⭐️ THÊM TRƯỜNG NÀY (Để lưu ghi chú từ DTO)
    String customerNote;

    // Địa chỉ giao hàng (Tùy chọn: có thể dùng @ManyToOne với Address Entity nếu bạn muốn lưu
    // địa chỉ đã chọn tại thời điểm đặt hàng, hoặc lưu dưới dạng chuỗi/JSON nếu muốn địa chỉ cố định)
    // Giả định lưu ID của địa chỉ đã chọn để tham chiếu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    Address shippingAddress;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();
}
