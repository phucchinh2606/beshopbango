// src/main/java/com/phucchinh/dogomynghe/repository/RefreshTokenRepository.java
package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.RefreshToken;
import com.phucchinh.dogomynghe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Tìm token theo User (để kiểm tra hoặc xóa token cũ)
    Optional<RefreshToken> findByUser(User user);

    // Xóa tất cả token của một User (dùng cho logout)
    void deleteByUser(User user);
}