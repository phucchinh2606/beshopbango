package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.ProductRequest;
import com.phucchinh.dogomynghe.dto.response.CategoryResponse;
import com.phucchinh.dogomynghe.dto.response.ProductResponse;
import com.phucchinh.dogomynghe.entity.Category;
import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.CategoryRepository;
import com.phucchinh.dogomynghe.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    CloudinaryService cloudinaryService;

    private ProductResponse mapToResponse(Product product) {
        CategoryResponse categoryResponse = null;
        if (product.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .createdAt(product.getCategory().getCreatedAt())
                    .build();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .category(categoryResponse)
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .build();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile file) throws AppException {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadFile(file, "products/");
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Product newProduct = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(imageUrl)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .category(category)
                .build();

        newProduct = productRepository.save(newProduct);
        return mapToResponse(newProduct);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long productId, String name, String description, Long price, Long categoryId,
            int stockQuantity, MultipartFile file) throws AppException {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Category newCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String newImageUrl = existingProduct.getImageUrl();
        if (file != null && !file.isEmpty()) {
            try {
                newImageUrl = cloudinaryService.uploadFile(file, "products/");
            } catch (IOException e) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setCategory(newCategory);
        existingProduct.setStockQuantity(stockQuantity);
        existingProduct.setImageUrl(newImageUrl);

        existingProduct = productRepository.save(existingProduct);
        return mapToResponse(existingProduct);
    }

    public Page<ProductResponse> getProducts(Specification<Product> spec, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::mapToResponse);
    }

    public ProductResponse getProductById(Long productId) throws AppException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long productId) throws AppException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                cloudinaryService.deleteFile(product.getImageUrl());
            } catch (IOException e) {
                log.error("Failed to delete image for product ID {}: {}", productId, e.getMessage());
            }
        }

        productRepository.delete(product);
    }
}
