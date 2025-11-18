package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.Cart;
import com.phucchinh.dogomynghe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Tìm giỏ hàng (Cart) đang hoạt động duy nhất của một User
    // Quan trọng: Vì mối quan hệ là OneToOne, nên chỉ có 1 Cart tồn tại cho mỗi User.
    Optional<Cart> findByUser(User user);
}