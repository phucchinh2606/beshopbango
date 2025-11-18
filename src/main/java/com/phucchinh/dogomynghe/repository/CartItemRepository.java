// CartItemRepository.java
package com.phucchinh.dogomynghe.repository;
import com.phucchinh.dogomynghe.entity.Cart;
import com.phucchinh.dogomynghe.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ⭐️ THÊM PHƯƠNG THỨC MỚI:
    // Tìm tất cả CartItem có ID nằm trong danh sách (List<Long>)
    // VÀ thuộc về một giỏ hàng (Cart) cụ thể.
    // Điều này ngăn người dùng thanh toán CartItem của người khác.
    List<CartItem> findAllByIdInAndCart(List<Long> ids, Cart cart);
}