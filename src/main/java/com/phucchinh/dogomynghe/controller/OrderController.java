package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.OrderCreationRequest;
import com.phucchinh.dogomynghe.dto.response.OrderResponse;
import com.phucchinh.dogomynghe.service.OrderService;
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
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    /**
     * POST /api/v1/user/orders
     * 1. Tạo đơn hàng mới từ giỏ hàng (Checkout)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(
            Authentication authentication,
            @RequestBody @Valid OrderCreationRequest request) {

        String username = authentication.getName();
        return orderService.createOrderFromCart(username, request);
    }

    /**
     * GET /api/v1/user/orders
     * 2. Lấy lịch sử tất cả đơn hàng của User
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponse> getOrderHistory(Authentication authentication) {

        String username = authentication.getName();
        return orderService.getOrderHistory(username);
    }

    /**
     * GET /api/v1/user/orders/{orderId}
     * 3. Xem chi tiết một đơn hàng cụ thể
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponse getOrderDetail(
            Authentication authentication,
            @PathVariable Long orderId) {

        String username = authentication.getName();
        return orderService.getOrderDetail(username, orderId);
    }

    /**
     * PATCH /api/v1/user/orders/{orderId}/cancel
     * 4. [USER] Hủy đơn hàng
     */
    @PatchMapping("/{orderId}/cancel") // Dùng PATCH vì chúng ta cập nhật một phần
    @PreAuthorize("hasRole('USER')")
    public OrderResponse cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        String username = authentication.getName();
        return orderService.userCancelOrder(username, orderId);
    }
}