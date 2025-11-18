package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.entity.RefreshToken;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenService {

    final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    long refreshTokenDurationMs;

    /**
     * SỬA LỖI: Tìm và Cập nhật token cũ, hoặc Tạo mới nếu chưa tồn tại.
     * Điều này tránh được lỗi "Duplicate entry" trong Transaction.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // 1. Tìm token hiện có của user
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken;

        if (existingTokenOpt.isPresent()) {
            // 2. Nếu có: Cập nhật token và ngày hết hạn
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        } else {
            // 3. Nếu không có: Tạo mới
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .token(UUID.randomUUID().toString())
                    .build();
        }

        // 4. Lưu (merge/persist)
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) throws AppException {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        return token;
    }

    @Transactional
    public void deleteByToken(String token) throws AppException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        refreshTokenRepository.delete(refreshToken);
    }
}