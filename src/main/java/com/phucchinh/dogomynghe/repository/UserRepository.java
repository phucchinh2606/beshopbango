package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm kiếm người dùng bằng username (dùng cho đăng nhập)
    Optional<User> findByUsername(String username);

    // Kiểm tra xem email đã tồn tại hay chưa (dùng cho đăng ký)
    boolean existsByEmail(String email);

    // Kiểm tra xem số điện thoại đã tồn tại hay chưa
    boolean existsByPhoneNumber(String phoneNumber);
}