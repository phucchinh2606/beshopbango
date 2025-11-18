package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.CategoryRequest;
import com.phucchinh.dogomynghe.dto.response.CategoryResponse;
import com.phucchinh.dogomynghe.entity.Category;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

    // --- Mapper ---
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .build();
    }

    // 1. Thêm Danh mục (Create)
    public CategoryResponse createCategory(CategoryRequest request) throws AppException {
        // Kiểm tra trùng lặp tên danh mục
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    // 2. Lấy tất cả Danh mục (Read All)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. Tìm kiếm theo tên (Search by Name)
    public List<CategoryResponse> searchCategoriesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 4. Sửa Danh mục (Update)
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) throws AppException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Kiểm tra trùng lặp tên danh mục (trừ tên của chính nó)
        if (categoryRepository.findByName(request.getName())
                .filter(c -> !c.getId().equals(categoryId))
                .isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }

        category.setName(request.getName());

        return mapToResponse(categoryRepository.save(category));
    }

    // 5. Xóa Danh mục (Delete)
    public void deleteCategory(Long categoryId) throws AppException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // *Lưu ý: Trong thực tế, bạn nên kiểm tra xem có sản phẩm nào thuộc danh mục này không trước khi xóa.
        categoryRepository.deleteById(categoryId);
    }
}