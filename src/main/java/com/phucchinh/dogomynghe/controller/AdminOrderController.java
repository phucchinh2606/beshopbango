package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.OrderStatusUpdateRequest;
import com.phucchinh.dogomynghe.dto.response.OrderResponse;
import com.phucchinh.dogomynghe.enums.OrderStatus;
import com.phucchinh.dogomynghe.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
// Import Page và Pageable
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders") // Đường dẫn riêng cho Admin
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ toàn bộ Controller
public class AdminOrderController {

    OrderService orderService; // Vẫn dùng OrderService

    /**
     * GET /api/v1/admin/orders
     * Lấy tất cả đơn hàng (có phân trang)
     * Lọc theo trạng thái nếu có: ?status=PENDING
     * Phân trang: ?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public Page<OrderResponse> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {

        if (status != null) {
            // Lọc theo trạng thái
            return orderService.adminGetOrdersByStatus(status, pageable);
        }
        // Lấy tất cả
        return orderService.adminGetAllOrders(pageable);
    }

    /**
     * GET /api/v1/admin/orders/{orderId}
     * Lấy chi tiết 1 đơn hàng bất kỳ
     */
    @GetMapping("/{orderId}")
    public OrderResponse getOrderDetail(@PathVariable Long orderId) {
        return orderService.adminGetOrderDetail(orderId);
    }

    /**
     * PATCH /api/v1/admin/orders/{orderId}/status
     * Cập nhật trạng thái đơn hàng
     */
    @PatchMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderStatusUpdateRequest request) {

        return orderService.adminUpdateOrderStatus(orderId, request.getNewStatus());
    }
}