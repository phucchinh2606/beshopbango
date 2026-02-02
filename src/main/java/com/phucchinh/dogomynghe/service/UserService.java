package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.TokenRefreshRequest;
import com.phucchinh.dogomynghe.dto.request.UserLoginRequest;
import com.phucchinh.dogomynghe.dto.request.UserRegistrationRequest;
import com.phucchinh.dogomynghe.dto.response.*;
import com.phucchinh.dogomynghe.entity.*;
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

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
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

    // =========================================================
    //               MAIN METHOD: getMyInfo
    // =========================================================
    @Transactional
    public UserResponse getMyInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kích hoạt Lazy Loading (nếu cần thiết để Hibernate tải dữ liệu)
        if (user.getAddresses() != null) user.getAddresses().size();
        if (user.getOrders() != null) user.getOrders().size();
        if (user.getCart() != null && user.getCart().getItems() != null) user.getCart().getItems().size();

        return mapToUserResponse(user);
    }

    // =========================================================
    //               ADMIN METHODS
    // =========================================================
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    public UserResponse updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setUserRole(newRole);
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    // =========================================================
    //           CÁC HÀM MAPPER (TỰ TẠO TẠI CHỖ)
    // =========================================================

    // 1. Map User
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())

                // Map danh sách địa chỉ
                .addresses(user.getAddresses() != null
                        ? user.getAddresses().stream().map(this::mapToAddressResponse).collect(Collectors.toList())
                        : List.of())

                // Map giỏ hàng
                .cart(user.getCart() != null
                        ? mapToCartResponse(user.getCart())
                        : null)

                // Map lịch sử đơn hàng (Sắp xếp mới nhất lên đầu)
                .orders(user.getOrders() != null
                        ? user.getOrders().stream()
                        .map(this::mapToOrderResponse)
                        .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    // 2. Map Address (Copy logic format string từ AddressService sang đây)
    private AddressResponse mapToAddressResponse(Address address) {
        String fullAddress = String.format("%s, %s, %s, %s",
                address.getNote(),
                address.getVillage(),
                address.getCommune(),
                address.getCity());

        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .commune(address.getCommune())
                .village(address.getVillage())
                .note(address.getNote())
                .fullAddress(fullAddress)
                .createdAt(address.getCreatedAt())
                .build();
    }

    // 3. Map Order
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId()) // Chú ý tên trường trong DTO là orderId hay id
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                // Tái sử dụng hàm map address ở trên
                .shippingAddress(order.getShippingAddress() != null ? mapToAddressResponse(order.getShippingAddress()) : null)
                .items(order.getItems().stream().map(this::mapToOrderItemResponse).collect(Collectors.toList()))
                .build();
    }

    // 4. Map Order Item
    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .product(mapToProductMinimalResponse(item.getProduct()))
                .build();
    }

    // 5. Map Cart
    private CartResponse mapToCartResponse(Cart cart) {
        // Kiểm tra null cho items
        List<CartItem> items = cart.getItems();
        if (items == null) {
            items = List.of(); // Nếu null thì coi như danh sách rỗng
        }

        List<CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        long total = itemResponses.stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalCartPrice(total)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    // 6. Map Cart Item
    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .product(mapToProductMinimalResponse(item.getProduct()))
                .build();
    }

    // 7. Map Product Minimal (Dùng chung cho cả Cart và Order)
    private ProductMinimalResponse mapToProductMinimalResponse(Product product) {
        return ProductMinimalResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .build();
    }
}