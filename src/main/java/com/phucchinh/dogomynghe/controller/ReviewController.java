package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.ReviewRequest;
import com.phucchinh.dogomynghe.dto.response.ReviewResponse;
import com.phucchinh.dogomynghe.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    /**
     * POST /api/v1/reviews
     * Gửi đánh giá (Yêu cầu đăng nhập & đã mua hàng)
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            Authentication authentication,
            @RequestBody @Valid ReviewRequest request) {

        String username = authentication.getName();
        return reviewService.createReview(username, request);
    }

    /**
     * GET /api/v1/reviews/product/{productId}
     * Xem đánh giá của sản phẩm (Công khai - ai cũng xem được)
     */
    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getProductReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }
}