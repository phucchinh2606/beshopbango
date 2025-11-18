// CategoryRepository.java
package com.phucchinh.dogomynghe.repository;
import com.phucchinh.dogomynghe.entity.Category;
import com.phucchinh.dogomynghe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm kiếm danh mục theo tên (dùng cho tìm kiếm và kiểm tra trùng lặp)
    Optional<Category> findByName(String name);

    // Tìm kiếm danh mục theo tên, hỗ trợ tìm kiếm không phân biệt chữ hoa/chữ thường (LIKE)
    List<Category> findByNameContainingIgnoreCase(String name);

    // Kiểm tra xem tên danh mục đã tồn tại chưa (khi tạo hoặc sửa)
    boolean existsByNameIgnoreCase(String name);

}