package com.phucchinh.dogomynghe.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtTokenProvider jwtTokenProvider;
    UserDetailServiceImpl userDetailsService;
    JwtBlacklistService jwtBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {

                // --- BƯỚC MỚI: KIỂM TRA BLACKLIST ---
                if (jwtBlacklistService.isTokenBlacklisted(jwt)) {
                    // Trả về lỗi 401 Unauthorized
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked.");
                    return;
                }
                // ------------------------------------

                String username = jwtTokenProvider.getUserNameFromJwtToken(jwt);

                // UserDetailServiceImpl phải load User, và User phải implement UserDetails (như Bước 1)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Tạo đối tượng xác thực (Authentication)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Thiết lập Authentication vào SecurityContext.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    // Trích xuất JWT từ Header "Authorization: Bearer <token>"
    String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
