package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.ProductRequest;
import com.phucchinh.dogomynghe.dto.response.ProductResponse;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    /**
     * POST: Thêm sản phẩm mới (chỉ ADMIN)
     * Endpoint này nhận dữ liệu dạng multipart/form-data
     */
    // KHÔNG dùng @RequestPart cho DTO nữa, dùng @RequestParam string
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Long price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("file") @Valid MultipartFile file) throws AppException {

        // Tạo ProductRequest thủ công từ các @RequestParam
        ProductRequest request = ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build();

        return productService.createProduct(request, file);
    }

    // ... (Thêm các endpoint GET, PUT, DELETE khác cho Product)
    /**
     * PUT: Cập nhật sản phẩm theo ID (chỉ ADMIN) - SỬ DỤNG CÁCH CŨ @RequestParam
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateProduct(
            @PathVariable Long productId,

            // Dữ liệu được gửi dưới dạng chuỗi Text trong form-data
            @RequestParam("name") @NotBlank(message = "Tên không được để trống!") String name,
            @RequestParam("description") String description,
            @RequestParam("price") @NotNull(message = "Giá không được để trống!") @Min(value = 0, message = "Giá phải lớn hơn 0!") Long price,
            @RequestParam("categoryId") @NotNull(message = "ID Category không được để trống!") Long categoryId,

            // File ảnh vẫn là MultipartFile
            @RequestParam(value = "file", required = false) @Nullable MultipartFile file) throws AppException {

        // Gọi phương thức Service mới với các tham số riêng lẻ
        return productService.updateProduct(productId, name, description, price, categoryId, file);
    }

    /**
     * GET: Lấy tất cả sản phẩm
     */
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * GET: Lấy sản phẩm theo ID
     */
    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable Long productId) throws AppException {
        return productService.getProductById(productId);
    }

    /**
     * GET: Tìm kiếm sản phẩm theo tên
     */
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam(value = "name", required = false) String name) {
        return productService.searchProductsByName(name);
    }

    /**
     * GET: Lấy sản phẩm theo danh mục ID
     */
    @GetMapping("/by-category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) throws AppException {
        return productService.getProductsByCategory(categoryId);
    }

    // --- CHỨC NĂNG DELETE (ADMIN ONLY) ---

    /**
     * DELETE: Xóa sản phẩm theo ID
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Trả về 204 No Content khi xóa thành công
    public void deleteProduct(@PathVariable Long productId) throws AppException, IOException {
        productService.deleteProduct(productId);
    }
}