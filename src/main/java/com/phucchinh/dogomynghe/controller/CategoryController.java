package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.CategoryRequest;
import com.phucchinh.dogomynghe.dto.response.CategoryResponse;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize; // Quan trọng cho phân quyền
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    // --- ADMIN CHỈ CÓ QUYỀN THỰC HIỆN CÁC THAO TÁC CRUD NÀY ---

    /**
     * POST: Tạo danh mục mới
     * Yêu cầu quyền ROLE_ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody @Valid CategoryRequest request) throws AppException {
        return categoryService.createCategory(request);
    }

    /**
     * PUT: Cập nhật danh mục
     * Yêu cầu quyền ROLE_ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(@PathVariable Long categoryId,
                                           @RequestBody @Valid CategoryRequest request) throws AppException {
        return categoryService.updateCategory(categoryId, request);
    }

    /**
     * DELETE: Xóa danh mục
     * Yêu cầu quyền ROLE_ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) throws AppException {
        categoryService.deleteCategory(categoryId);
    }

    // --- CÁC THAO TÁC READ CÓ THỂ CÔNG KHAI HOẶC CHO CẢ USER/ADMIN ---

    /**
     * GET: Lấy tất cả danh mục
     * Cho phép tất cả người dùng (hoặc công khai)
     * (Tôi sẽ đặt là công khai .permitAll() hoặc chỉ cần authenticated() nếu không có .permitAll() ở Security Config)
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * GET: Tìm kiếm danh mục theo tên
     * Cho phép tất cả người dùng (hoặc công khai)
     */
    @GetMapping("/search")
    public List<CategoryResponse> searchCategories(@RequestParam("name") String name) {
        return categoryService.searchCategoriesByName(name);
    }
}