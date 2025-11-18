package com.phucchinh.dogomynghe.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class JwtBlacklistService {

    // Sử dụng In-Memory Map để lưu trữ các token đã bị hủy.
    // Key: Chuỗi JWT, Value: Thời điểm hết hạn thực tế của JWT.
    private final ConcurrentHashMap<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    /**
     * Thêm JWT vào Blacklist khi người dùng Logout.
     */
    public void blacklistToken(String token, Instant expiryDate) {
        if (expiryDate.isAfter(Instant.now())) {
            revokedTokens.put(token, expiryDate);
            log.info("JWT blacklisted until: {}", expiryDate);
        }
    }

    /**
     * Kiểm tra xem JWT có nằm trong Blacklist hay không.
     */
    public boolean isTokenBlacklisted(String token) {
        Instant expiryTime = revokedTokens.get(token);

        if (expiryTime == null) {
            return false; // Token không bị hủy
        }

        // Tự động dọn dẹp cache: Nếu token đã hết hạn theo thời gian thực tế, xóa khỏi cache.
        if (expiryTime.isBefore(Instant.now())) {
            revokedTokens.remove(token);
            return false;
        }

        return true; // Token đã bị hủy
    }
}