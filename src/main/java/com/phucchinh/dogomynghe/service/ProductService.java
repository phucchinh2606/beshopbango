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
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    CloudinaryService cloudinaryService;

    // --- Mapper (Tái sử dụng hoặc tạo mới) ---

    private ProductResponse mapToResponse(Product product) {
        // Giả định bạn có CategoryService hoặc CategoryResponse Mapper
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .createdAt(product.getCategory().getCreatedAt())
                .build();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .category(categoryResponse)
                .createdAt(product.getCreatedAt())
                .build();
    }

    /**
     * Thêm sản phẩm mới kèm upload ảnh lên S3
     * @param request Dữ liệu sản phẩm
     * @param file File ảnh MultipartFile
     * @return ProductResponse
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile file) throws AppException {

        // 1. Kiểm tra tồn tại Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. Upload ảnh lên S3
        String imageUrl;
        try {
            // Upload vào thư mục 'products/'
            imageUrl = cloudinaryService.uploadFile(file, "products/");
        } catch (IOException e) {
            // Xử lý lỗi upload
            throw new AppException(ErrorCode.UPLOAD_FAILED); // Bạn cần tạo mã lỗi này
        }

        // 3. Tạo Entity Product
        Product newProduct = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(imageUrl) // Lưu URL của ảnh
                .category(category)
                .build();

        // 4. Lưu vào CSDL
        newProduct = productRepository.save(newProduct);

        return mapToResponse(newProduct);
    }

    /**
     * Cập nhật thông tin và ảnh của sản phẩm
     * @param productId ID của sản phẩm
     * @param request Dữ liệu mới
     * @param file File ảnh mới (có thể null)
     * @return ProductResponse đã cập nhật
     */
    /**
     * Cập nhật thông tin và ảnh của sản phẩm - SỬ DỤNG THAM SỐ RIÊNG LẺ
     */
    @Transactional
    public ProductResponse updateProduct(
            Long productId,
            String name,          // Tham số mới
            String description,   // Tham số mới
            Long price,           // Tham số mới
            Long categoryId,      // Tham số mới
            MultipartFile file) throws AppException {

        // 1. Tìm Sản phẩm hiện tại
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. Tìm Category mới
        Category newCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // 3. Xử lý Ảnh (Chỉ upload nếu có file mới)
        String newImageUrl = existingProduct.getImageUrl(); // Giữ URL cũ

        if (file != null && !file.isEmpty()) {
            try {
                // Upload ảnh mới
                newImageUrl = cloudinaryService.uploadFile(file, "products/");
            } catch (IOException e) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        // 4. Cập nhật dữ liệu Entity
        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setCategory(newCategory);
        existingProduct.setImageUrl(newImageUrl);

        // *Lưu ý: Nếu bạn dùng @PreUpdate, trường updatedAt sẽ được cập nhật tự động tại đây.

        // 5. Lưu và trả về
        existingProduct = productRepository.save(existingProduct);

        return mapToResponse(existingProduct);
    }

    // 1. Lấy tất cả Sản phẩm
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. Lấy sản phẩm theo ID
    public ProductResponse getProductById(Long productId) throws AppException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToResponse(product);
    }

    // 3. Tìm kiếm sản phẩm theo Tên
    public List<ProductResponse> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 4. Lấy sản phẩm theo Danh mục
    public List<ProductResponse> getProductsByCategory(Long categoryId) throws AppException {
        // Kiểm tra Category có tồn tại không
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5. Xóa Sản phẩm (Chỉ Admin)
    @Transactional
    public void deleteProduct(Long productId) throws AppException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa ảnh trên Cloudinary TRƯỚC
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                cloudinaryService.deleteFile(product.getImageUrl());
            } catch (IOException e) {
                // Chúng ta có thể log lỗi nhưng vẫn tiếp tục xóa Entity để tránh lỗi khóa
                log.error("Failed to delete image for product ID {}: {}", productId, e.getMessage());
            }
        }

        // Xóa Entity
        productRepository.delete(product);
    }
}