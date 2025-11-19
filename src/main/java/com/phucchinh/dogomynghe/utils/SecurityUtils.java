package com.phucchinh.dogomynghe.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    @Value("${jwt.secret-key}")
    private String secret;

    private final HttpServletRequest request;

    // Tạo Key từ secret
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Lấy userId hiện tại
    public  Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            try {
                return Long.parseLong((String) auth.getPrincipal());
            } catch (NumberFormatException e) {
                log.error("Principal is not a valid Long: {}", auth.getPrincipal());
                return null;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String idStr = claims.get("id", String.class);
            if (idStr == null) return null;
            return Long.parseLong(idStr);
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }
}
