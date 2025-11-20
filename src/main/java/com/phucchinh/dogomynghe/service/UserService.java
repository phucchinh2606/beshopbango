package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.TokenRefreshRequest;
import com.phucchinh.dogomynghe.dto.request.UserLoginRequest;
import com.phucchinh.dogomynghe.dto.request.UserRegistrationRequest;
import com.phucchinh.dogomynghe.dto.response.AuthResponse;
import com.phucchinh.dogomynghe.dto.response.UserResponse;
import com.phucchinh.dogomynghe.entity.Cart;
import com.phucchinh.dogomynghe.entity.RefreshToken;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.enums.UserRole;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.UserRepository;
import com.phucchinh.dogomynghe.security.JwtBlacklistService;
import com.phucchinh.dogomynghe.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    // Giả định bạn đã cấu hình PasswordEncoder trong Spring Security
    // Ví dụ: BCryptPasswordEncoder
    PasswordEncoder passwordEncoder;

    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;

    RefreshTokenService refreshTokenService;
    JwtBlacklistService jwtBlacklistService;

    /**
     * Chức năng Đăng ký người dùng mới.
     * @param request DTO chứa thông tin đăng ký.
     * @return UserResponse chứa thông tin người dùng đã đăng ký.
     * @throws AppException nếu email hoặc số điện thoại đã tồn tại.
     */
    public UserResponse register(UserRegistrationRequest request) throws AppException {
        // 1. Kiểm tra sự tồn tại của Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // 2. Kiểm tra sự tồn tại của Số điện thoại
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        // 3. Tạo Entity User từ Request DTO
        User newUser = User.builder()
                .username(request.getUsername())
                // Mã hóa mật khẩu trước khi lưu
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                // Thiết lập vai trò mặc định
                .userRole(UserRole.ROLE_USER)
                .build();

        // 4. Khởi tạo giỏ hàng (Cart) cho người dùng
        Cart newCart = Cart.builder()
                .user(newUser)
                .build();
        newUser.setCart(newCart);

        // 5. Lưu User vào cơ sở dữ liệu
        newUser = userRepository.save(newUser);

        // 6. Trả về UserResponse
        return mapToUserResponse(newUser);
    }

    /*
            * Cập nhật Chức năng Đăng nhập để tạo và trả về Refresh Token
     */
    @Transactional
    public AuthResponse login(UserLoginRequest request) {

        // 1. Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication."));

        // 2. Tạo Access Token (JWT)
        String jwt = jwtTokenProvider.generateToken(authentication);

        // 3. Tạo và Lưu Refresh Token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .expiryAt(jwtTokenProvider.extractExpiration(jwt).toInstant()) // Dùng phương thức mới
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .build();
    }

    // --- PHƯƠNG THỨC REFRESH TOKEN (Giả định đã có RefreshTokenService) ---
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        User user = refreshToken.getUser();

        // Dùng phương thức overload generateToken(UserDetails)
        String newAccessToken = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .expiryAt(jwtTokenProvider.extractExpiration(newAccessToken).toInstant())
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .build();
    }

    // --- PHƯƠNG THỨC LOGOUT (Cập nhật Blacklist) ---
    @Transactional
    public void logout(String jwt, String refreshToken) {

        // 1. Xóa Refresh Token (RT) khỏi CSDL
        refreshTokenService.deleteByToken(refreshToken);

        // 2. Blacklist Access Token (JWT)
        try {
            // Lấy thời gian hết hạn của JWT
            java.util.Date expiryDate = jwtTokenProvider.extractExpiration(jwt);
            // Thêm vào blacklist (thời gian hết hạn là thời gian còn lại của JWT)
            jwtBlacklistService.blacklistToken(jwt, expiryDate.toInstant());
            log.info("User logged out. JWT blacklisted successfully.");
        } catch (Exception e) {
            // Trường hợp JWT không hợp lệ (ví dụ: client gửi JWT hết hạn)
            log.warn("Failed to blacklist JWT: {}", e.getMessage());
        }
    }

    /**
     * Lấy thông tin chi tiết của người dùng hiện tại.
     * @param username Tên người dùng được trích xuất từ JWT Token.
     * @return UserResponse chứa thông tin chi tiết.
     */
    public UserResponse getMyInfo(String username) {
        // Tìm kiếm User dựa trên username (được đảm bảo tồn tại từ quá trình xác thực JWT)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication."));

        // Chuyển đổi Entity sang DTO và trả về
        return mapToUserResponse(user);
    }


    /**
     * Chuyển đổi Entity User sang DTO UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}