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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    // --- ADMIN CHỈ CÓ QUYỀN THỰC HIỆN CÁC THAO TÁC CRUD NÀY ---

    /**
     * POST: Tạo danh mục mới với hình ảnh
     * Yêu cầu quyền ROLE_ADMIN hoặc EMPLOYEE
     * Content-Type: multipart/form-data
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @RequestParam String name,
            @RequestParam(value = "image", required = false) MultipartFile image) throws AppException {
        CategoryRequest request = CategoryRequest.builder()
                .name(name)
                .build();
        return categoryService.createCategory(request, image);
    }

    /**
     * PUT: Cập nhật danh mục với hình ảnh
     * Yêu cầu quyền ROLE_ADMIN hoặc EMPLOYEE
     * Content-Type: multipart/form-data
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(
            @PathVariable Long categoryId,
            @RequestParam String name,
            @RequestParam(value = "image", required = false) MultipartFile image) throws AppException {
        CategoryRequest request = CategoryRequest.builder()
                .name(name)
                .build();
        return categoryService.updateCategory(categoryId, request, image);
    }

    /**
     * DELETE: Xóa danh mục
     * Yêu cầu quyền ROLE_ADMIN hoặc EMPLOYEE
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) throws AppException {
        categoryService.deleteCategory(categoryId);
    }

    // --- CÁC THAO TÁC READ CÓ THỂ CÔNG KHAI HOẶC CHO CẢ USER/ADMIN ---

    /**
     * GET: Lấy tất cả danh mục
     * Cho phép tất cả người dùng (hoặc công khai)
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