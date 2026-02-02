package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.dto.response.stats.ChartData;
import com.phucchinh.dogomynghe.dto.response.stats.ProductStats;
import com.phucchinh.dogomynghe.entity.Category;
import com.phucchinh.dogomynghe.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Tìm kiếm sản phẩm theo tên (không phân biệt chữ hoa/thường)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm kiếm sản phẩm theo Category
    List<Product> findByCategory(Category category);

    // Tìm kiếm sản phẩm trong một khoảng giá
    List<Product> findByPriceBetween(Long minPrice, Long maxPrice);

    // 1. Đếm tổng sản phẩm
    long count();

    // 2. Tìm sản phẩm sắp hết hàng (Stock <= 5)
    List<Product> findByStockQuantityLessThanEqual(int quantity);

    // Trong ProductRepository.java

    @Query("SELECT new com.phucchinh.dogomynghe.dto.response.stats.ProductStats(" +
            "p.id, p.name, SUM(CAST(oi.quantity AS long)), CAST(p.stockQuantity AS long), " +
            "SUM(oi.priceAtPurchase * oi.quantity), p.imageUrl) " +
            "FROM OrderItem oi " +
            "JOIN oi.product p " +
            "JOIN oi.order o " +
            "WHERE o.status = com.phucchinh.dogomynghe.enums.OrderStatus.DELIVERED " + // Hoặc COMPLETED tùy Enum của bạn
            "GROUP BY p.id, p.name, p.stockQuantity, p.imageUrl " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<ProductStats> findBestSellers(Pageable pageable);

    @Query("SELECT new com.phucchinh.dogomynghe.dto.response.stats.ChartData(" +
            "c.name, SUM(oi.priceAtPurchase * oi.quantity)) " +
            "FROM OrderItem oi " +
            "JOIN oi.product p " +
            "JOIN p.category c " +
            "JOIN oi.order o " +
            "WHERE o.status = com.phucchinh.dogomynghe.enums.OrderStatus.DELIVERED " +
            "GROUP BY c.name")
    List<ChartData> getRevenueByCategory();
}