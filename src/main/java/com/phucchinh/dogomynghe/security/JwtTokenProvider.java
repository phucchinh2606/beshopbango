package com.phucchinh.dogomynghe.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Lấy secret key đã mã hóa
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 1. Tạo JWT Token
    public String generateToken(Authentication authentication) {
        // Lấy thông tin UserDetails từ đối tượng Authentication
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        // Lấy quyền của user để cho vào token
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Đặt Subject là Username
                .claim("roles", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Thời gian hết hạn
                .signWith(key(), SignatureAlgorithm.HS256) // Ký với thuật toán HS256
                .compact();
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject((userDetails.getUsername())) // Đặt Subject là Username
                .setIssuedAt(new Date())
                // jwtExpirationMs là thời hạn của Access Token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Lấy Username từ JWT Token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. Xác thực JWT Token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        }
        return false;
    }

    // --- PHƯƠNG THỨC BỔ SUNG: Trích xuất thời gian hết hạn ---
    public Date extractExpiration(String token) {
        // Lấy Claims (Body)
        Claims claims = Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody();
        // Lấy thời gian hết hạn (Expiration)
        return claims.getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody();
    }


}
