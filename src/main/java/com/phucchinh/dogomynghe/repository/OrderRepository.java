package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.Order;
import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.OrderStatus;
// Import thêm Page và Pageable
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // --- Giữ nguyên các phương thức của User ---
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);

    // --- BỔ SUNG PHƯƠNG THỨC CHO ADMIN (CÓ PHÂN TRANG) ---

    // 1. Lấy tất cả đơn hàng (có phân trang)
    // JpaRepository đã hỗ trợ: Page<Order> findAll(Pageable pageable);

    // 2. Lấy đơn hàng theo trạng thái (có phân trang)
    // (File bạn gửi đã có findByUserAndStatus, giờ chúng ta cần bản không theo User)
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Kiểm tra xem User có đơn hàng nào ở trạng thái (DELIVERED)
     * và chứa sản phẩm (Product) cụ thể không.
     * Spring Data JPA sẽ tự động join các bảng: Order -> OrderItem -> Product
     */
    boolean existsByUserAndStatusAndItems_Product(User user, OrderStatus status, Product product);
}