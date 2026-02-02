package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.VNPayPaymentRequest;
import com.phucchinh.dogomynghe.dto.response.VNPayPaymentResponse;
import com.phucchinh.dogomynghe.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    /**
     * POST /api/user/payment/vnpay/create
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/vnpay/create")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public VNPayPaymentResponse createVNPayPayment(
            @RequestBody VNPayPaymentRequest request,
            HttpServletRequest httpRequest) {

        return paymentService.createVNPayPayment(request, httpRequest);
    }

    /**
     * GET /api/user/payment/vnpay/return
     * Xử lý kết quả trả về từ VNPay (cho user)
     */
    @GetMapping("/vnpay/return")
    @PreAuthorize("hasRole('USER')")
    public Map<String, String> handleVNPayReturn(@RequestParam Map<String, String> params) {
        return paymentService.processVNPayReturn(params);
    }

    /**
     * GET /api/user/payment/vnpay/callback
     * Xử lý callback từ VNPay (cho server)
     */
    @GetMapping("/vnpay/callback")
    public String handleVNPayCallback(@RequestParam Map<String, String> params) {
        return paymentService.processVNPayCallback(params);
    }
}