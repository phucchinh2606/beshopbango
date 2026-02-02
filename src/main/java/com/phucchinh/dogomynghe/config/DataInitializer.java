package com.phucchinh.dogomynghe.config;

import com.phucchinh.dogomynghe.entity.Cart;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.UserRole;
import com.phucchinh.dogomynghe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Import(com.phucchinh.dogomynghe.security.SecurityConfig.class)
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Định nghĩa Bean CommandLineRunner để chạy code sau khi Spring Context được tải
    @Bean
    @Transactional
    public CommandLineRunner initData() {
        return args -> {
            final String adminUsername = "admin";

            // 1. Kiểm tra nếu Admin chưa tồn tại
            if (userRepository.findByUsername(adminUsername).isEmpty()) {

                // 2. Tạo Entity User cho Admin
                User adminUser = User.builder()
                        .username(adminUsername)
                        // Mã hóa mật khẩu, mật khẩu mặc định: "milodola"
                        .password(passwordEncoder.encode("milodola"))
                        .email("admin@dogomynghe.com")
                        .phoneNumber("0899727854")
                        .userRole(UserRole.ROLE_ADMIN) // Thiết lập vai trò ADMIN
                        .build();

                // 3. Khởi tạo Giỏ hàng (Cart) cho Admin (đảm bảo không bị lỗi khóa ngoại)
                Cart adminCart = Cart.builder()
                        .user(adminUser)
                        .build();
                adminUser.setCart(adminCart);

                // 4. Lưu vào cơ sở dữ liệu
                userRepository.save(adminUser);

                System.out.println("==============================================");
                System.out.println("ADMIN User created successfully!");
                System.out.println("Username: admin");
                System.out.println("Password: milodola");
                System.out.println("Role: ROLE_ADMIN");
                System.out.println("==============================================");
            }
        };
    }
}