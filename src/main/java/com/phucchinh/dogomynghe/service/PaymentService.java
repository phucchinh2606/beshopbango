package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.config.VNPayConfig;
import com.phucchinh.dogomynghe.dto.request.VNPayPaymentRequest;
import com.phucchinh.dogomynghe.dto.response.VNPayPaymentResponse;
import com.phucchinh.dogomynghe.entity.Order;
import com.phucchinh.dogomynghe.enums.OrderStatus;
import com.phucchinh.dogomynghe.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    OrderRepository orderRepository;
    VNPayConfig vnpayConfig;

    public VNPayPaymentResponse createVNPayPayment(VNPayPaymentRequest request, HttpServletRequest httpRequest) {
        try {
            // Validate order exists and belongs to user
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (!OrderStatus.PENDING.equals(order.getStatus())) {
                throw new RuntimeException("Order is not in pending status");
            }

            // Create VNPay parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // VNPay expects amount in smallest currency unit
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", String.valueOf(request.getOrderId()));
            vnpParams.put("vnp_OrderInfo", request.getOrderInfo());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
            vnpParams.put("vnp_IpAddr", getClientIpAddress(httpRequest));

            if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
                vnpParams.put("vnp_BankCode", request.getBankCode());
            }

            // Create timestamp
            LocalDateTime now = LocalDateTime.now();
            String vnpCreateDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            // Sort parameters
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            // Create query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                         .append('=')
                         .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            // Create secure hash
            String vnpSecureHash = VNPayUtils.hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnpSecureHash);

            String paymentUrl = vnpayConfig.getPayUrl() + "?" + query.toString();

            return VNPayPaymentResponse.builder()
                    .paymentUrl(paymentUrl)
                    .transactionId(String.valueOf(request.getOrderId()))
                    .orderId(request.getOrderId())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create VNPay payment: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> processVNPayReturn(Map<String, String> params) {
        System.out.println("=== processVNPayReturn called ===");
        System.out.println("Params: " + params);
        
        Map<String, String> result = new HashMap<>();

        try {
            // Verify secure hash
            String vnpSecureHash = params.get("vnp_SecureHash");
            if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
                System.out.println("Missing vnp_SecureHash");
                result.put("status", "invalid");
                result.put("message", "Thiếu chữ ký bảo mật!");
                return result;
            }

            // Create a copy to avoid modifying the original params
            Map<String, String> paramsCopy = new HashMap<>(params);
            paramsCopy.remove("vnp_SecureHash");

            // Sort parameters
            List<String> fieldNames = new ArrayList<>(paramsCopy.keySet());
            Collections.sort(fieldNames);

            // Create hash data
            // VNPay returns URL-encoded params, but Spring decodes them automatically
            // To verify hash, we need to encode the values again to match how hash was calculated when sending request
            StringBuilder hashData = new StringBuilder();
            int index = 0;
            for (String fieldName : fieldNames) {
                String fieldValue = paramsCopy.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    // Encode fieldValue to match the hash calculation when creating payment URL
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (index < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
                index++;
            }

            String hashDataString = hashData.toString();
            String calculatedHash = VNPayUtils.hmacSHA512(vnpayConfig.getHashSecret(), hashDataString);

            // Compare hash case-insensitively (VNPay may return uppercase or lowercase)
            System.out.println("Hash verification - Calculated: " + calculatedHash + ", Received: " + vnpSecureHash);
            
            if (calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                System.out.println("Hash verified successfully");
                String responseCode = paramsCopy.get("vnp_ResponseCode");
                String transactionStatus = paramsCopy.get("vnp_TransactionStatus");
                String orderId = paramsCopy.get("vnp_TxnRef");
                
                System.out.println("ResponseCode: " + responseCode + ", TransactionStatus: " + transactionStatus + ", OrderId: " + orderId);

                if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                    System.out.println("Payment successful, updating order...");
                    // Update order status to CONFIRMED and payment status to PAID
                    try {
                        Long orderIdLong = Long.valueOf(orderId);
                        System.out.println("Looking for order with ID: " + orderIdLong);
                        
                        Order order = orderRepository.findById(orderIdLong)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
                        
                        System.out.println("Order found! Current status: " + order.getStatus());
                        
                        // Only update if order is still PENDING (avoid duplicate updates from callback)
                        if (OrderStatus.PENDING.equals(order.getStatus())) {
                            System.out.println("Order is PENDING, updating to CONFIRMED...");
                            order.setStatus(OrderStatus.CONFIRMED);
                            order.setPaymentStatus("PAID");
                            order.setPaymentMethod("VNPAY");
                            order.setVnpTransactionNo(paramsCopy.get("vnp_TransactionNo"));
                            order.setVnpResponseCode(responseCode);
                            order.setVnpSecureHash(vnpSecureHash);
                            order.setPaidAt(LocalDateTime.now());
                            Order savedOrder = orderRepository.save(order);
                            System.out.println("Order " + orderId + " updated to CONFIRMED successfully. Saved order status: " + savedOrder.getStatus());
                        } else {
                            System.out.println("Order " + orderId + " already processed, status: " + order.getStatus());
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing orderId: " + orderId + " - " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        // Log error but don't fail the return process
                        System.err.println("Error updating order payment success: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    result.put("status", "success");
                    result.put("message", "Thanh toán thành công!");
                } else {
                    System.out.println("Payment failed - ResponseCode: " + responseCode + ", TransactionStatus: " + transactionStatus);
                    // Update order payment status to FAILED
                    if (orderId != null) {
                        try {
                            Order order = orderRepository.findById(Long.valueOf(orderId))
                                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
                            order.setPaymentStatus("FAILED");
                            order.setVnpResponseCode(responseCode);
                            orderRepository.save(order);
                        } catch (Exception e) {
                            System.err.println("Error updating order payment failed: " + e.getMessage());
                        }
                    }
                    
                    result.put("status", "failed");
                    result.put("message", "Thanh toán thất bại! ResponseCode: " + responseCode + ", TransactionStatus: " + transactionStatus);
                }
            } else {
                System.out.println("Hash verification failed!");
                result.put("status", "invalid");
                result.put("message", "Chữ ký không hợp lệ!");
            }

        } catch (Exception e) {
            System.err.println("Exception in processVNPayReturn: " + e.getMessage());
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Lỗi xử lý: " + e.getMessage());
        }

        System.out.println("=== processVNPayReturn returning result: " + result + " ===");
        return result;
    }

    public String processVNPayCallback(Map<String, String> params) {
        try {
            // Verify secure hash
            String vnpSecureHash = params.get("vnp_SecureHash");
            if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
                return "97"; // Checksum failed
            }

            // Create a copy to avoid modifying the original params
            Map<String, String> paramsCopy = new HashMap<>(params);
            paramsCopy.remove("vnp_SecureHash");

            List<String> fieldNames = new ArrayList<>(paramsCopy.keySet());
            Collections.sort(fieldNames);

            // Create hash data - encode values to match the hash calculation when creating payment URL
            StringBuilder hashData = new StringBuilder();
            int index = 0;
            for (String fieldName : fieldNames) {
                String fieldValue = paramsCopy.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (index < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
                index++;
            }

            String calculatedHash = VNPayUtils.hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());

            if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                return "97"; // Checksum failed
            }

            String responseCode = paramsCopy.get("vnp_ResponseCode");
            String transactionStatus = paramsCopy.get("vnp_TransactionStatus");
            String orderId = paramsCopy.get("vnp_TxnRef");

            // Update order status
            Order order = orderRepository.findById(Long.valueOf(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                // Payment successful - only update if still PENDING (avoid duplicate updates)
                if (OrderStatus.PENDING.equals(order.getStatus())) {
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.setPaymentStatus("PAID");
                    order.setPaymentMethod("VNPAY");
                    order.setVnpTransactionNo(paramsCopy.get("vnp_TransactionNo"));
                    order.setVnpResponseCode(responseCode);
                    order.setVnpSecureHash(vnpSecureHash);
                    order.setPaidAt(LocalDateTime.now());
                    orderRepository.save(order);
                }
            } else {
                // Payment failed
                order.setPaymentStatus("FAILED");
                order.setVnpResponseCode(responseCode);
                orderRepository.save(order);
            }

            return "00"; // Success response to VNPay

        } catch (Exception e) {
            return "99"; // Unknown error
        }
    }


    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}