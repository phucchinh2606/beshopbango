package com.phucchinh.dogomynghe.controller;


import com.phucchinh.dogomynghe.dto.request.TokenRefreshRequest;
import com.phucchinh.dogomynghe.dto.request.UserLoginRequest;
import com.phucchinh.dogomynghe.dto.request.UserRegistrationRequest;
import com.phucchinh.dogomynghe.dto.request.UserRoleUpdateRequest;
import com.phucchinh.dogomynghe.dto.response.AuthResponse;
import com.phucchinh.dogomynghe.dto.response.UserResponse;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    /**
     * Endpoint Đăng ký người dùng
     * POST /api/users/register
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // Trả về mã 201 Created
    public UserResponse registerUser(@RequestBody @Valid UserRegistrationRequest request) throws AppException {
        return userService.register(request);
    }

    /**
     * Endpoint Đăng nhập
     * POST /api/users/login
     */
    @PostMapping("/login")
    // Thay đổi kiểu trả về từ UserResponse sang AuthResponse
    public AuthResponse loginUser(@RequestBody @Valid UserLoginRequest request) {
        // Trả về AuthResponse chứa JWT Token
        return userService.login(request);
    }

    /**
     * Dùng Refresh Token để lấy Access Token mới
     */
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        return userService.refreshToken(request);
    }

    // --- ENDPOINT LOGOUT CÓ BLACKLIST ---
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    public void logout(
            // Access Token được gửi trong Header Authorization
            @RequestHeader("Authorization") String authorizationHeader,
            // Refresh Token được gửi trong Body
            @RequestBody @Valid TokenRefreshRequest request) {

        // Trích xuất chuỗi JWT từ "Bearer <token>"
        String jwt = authorizationHeader.substring(7);

        userService.logout(jwt, request.getRefreshToken());
    }

    /**
     * GET /api/v1/user/my-info
     * Lấy thông tin chi tiết của người dùng đang đăng nhập.
     */
    @GetMapping("/my-info")
    // Chỉ người dùng đã đăng nhập (có role USER hoặc ADMIN) mới được truy cập
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserResponse getMyInfo(Authentication authentication) {
        // Đối tượng Authentication chứa thông tin về người dùng đã được xác thực,
        // bao gồm username
        String username = authentication.getName();

        return userService.getMyInfo(username);
    }

    /**
     * GET /api/users/all
     * Lấy danh sách tất cả người dùng (chỉ dành cho ADMIN)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * PUT /api/users/{id}/role
     * Cập nhật vai trò của người dùng (chỉ dành cho ADMIN)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRole(@PathVariable Long id, @RequestBody @Valid UserRoleUpdateRequest request) {
        return userService.updateUserRole(id, request.getNewRole());
    }

    /**
     * DELETE /api/users/{id}
     * Xóa người dùng (chỉ dành cho ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
