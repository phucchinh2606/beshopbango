package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.ReviewRequest;
import com.phucchinh.dogomynghe.dto.response.ReviewResponse;
import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.entity.Review;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.enums.OrderStatus;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.OrderRepository;
import com.phucchinh.dogomynghe.repository.ProductRepository;
import com.phucchinh.dogomynghe.repository.ReviewRepository;
import com.phucchinh.dogomynghe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    ReviewRepository reviewRepository;
    OrderRepository orderRepository; // Cần để kiểm tra lịch sử mua
    ProductRepository productRepository;
    UserRepository userRepository;

    /**
     * Tạo đánh giá mới (Chỉ cho phép nếu đã mua và đơn hàng đã giao thành công)
     */
    @Transactional
    public ReviewResponse createReview(String username, ReviewRequest request) {
        // 1. Lấy User & Product (Giữ nguyên)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. Kiểm tra Đã mua & Đã giao (Giữ nguyên)
        boolean hasPurchased = orderRepository.existsByUserAndStatusAndItems_Product(
                user, OrderStatus.DELIVERED, product
        );
        if (!hasPurchased) {
            throw new AppException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        // 👇 3. LOGIC MỚI: Kiểm tra đã review chưa
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTED);
        }

        // 4. Lưu review (Giữ nguyên)
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return mapToReviewResponse(reviewRepository.save(review));
    }

    /**
     * Lấy danh sách đánh giá của một sản phẩm (Public)
     */
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .username(review.getUser().getUsername()) // Hiển thị tên người dùng
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}