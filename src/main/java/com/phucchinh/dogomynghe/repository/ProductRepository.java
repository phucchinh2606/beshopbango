package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.Category;
import com.phucchinh.dogomynghe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm kiếm sản phẩm theo tên (không phân biệt chữ hoa/thường)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm kiếm sản phẩm theo Category
    List<Product> findByCategory(Category category);

    // Tìm kiếm sản phẩm trong một khoảng giá
    List<Product> findByPriceBetween(Long minPrice, Long maxPrice);
}