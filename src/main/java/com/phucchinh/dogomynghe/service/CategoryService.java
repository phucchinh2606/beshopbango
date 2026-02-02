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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;
    CloudinaryService cloudinaryService;

    // --- Mapper ---
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .createdAt(category.getCreatedAt())
                .build();
    }

    // 1. Thêm Danh mục (Create) - với hình ảnh
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, MultipartFile file) throws AppException {
        // Kiểm tra trùng lặp tên danh mục
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }

        String imageUrl = null;
        
        // Upload hình ảnh nếu có
        if (file != null && !file.isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadFile(file, "categories/");
            } catch (IOException e) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        Category category = Category.builder()
                .name(request.getName())
                .imageUrl(imageUrl)
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

    // 4. Sửa Danh mục (Update) - với hình ảnh
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request, MultipartFile file) throws AppException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Kiểm tra trùng lặp tên danh mục (trừ tên của chính nó)
        if (categoryRepository.findByName(request.getName())
                .filter(c -> !c.getId().equals(categoryId))
                .isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }

        category.setName(request.getName());
        
        // Xử lý hình ảnh nếu có upload file mới
        if (file != null && !file.isEmpty()) {
            try {
                // Xóa ảnh cũ nếu tồn tại
                if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                    cloudinaryService.deleteFile(category.getImageUrl());
                }
                
                // Upload ảnh mới
                String newImageUrl = cloudinaryService.uploadFile(file, "categories/");
                category.setImageUrl(newImageUrl);
            } catch (IOException e) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        return mapToResponse(categoryRepository.save(category));
    }

    // 5. Xóa Danh mục (Delete)
    @Transactional
    public void deleteCategory(Long categoryId) throws AppException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // Xóa hình ảnh khỏi Cloudinary nếu tồn tại
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            try {
                cloudinaryService.deleteFile(category.getImageUrl());
            } catch (IOException e) {
                // Log lỗi nhưng vẫn tiếp tục xóa category
                System.err.println("Lỗi xóa ảnh: " + e.getMessage());
            }
        }
        
        categoryRepository.deleteById(categoryId);
    }
}