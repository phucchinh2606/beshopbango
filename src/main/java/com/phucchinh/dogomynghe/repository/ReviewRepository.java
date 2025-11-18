package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy tất cả đánh giá của một sản phẩm, sắp xếp theo thời gian mới nhất
    List<Review> findByProductOrderByCreatedAtDesc(Product product);

    // Tính điểm đánh giá trung bình cho một sản phẩm (cần dùng @Query hoặc Service)
    // Ví dụ: @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    // Double getAverageRatingByProduct(@Param("product") Product product);
}