package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.ProductRequest;
import com.phucchinh.dogomynghe.dto.response.ProductResponse;
import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.service.ProductService;
import com.phucchinh.dogomynghe.specification.ProductSpecification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;
    ProductSpecification productSpecification;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Long price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "stockQuantity", required = false, defaultValue = "0") Integer stockQuantity,
            @RequestParam("file") @Valid MultipartFile file) throws AppException {

        ProductRequest request = ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .stockQuantity(stockQuantity)
                .build();

        return productService.createProduct(request, file);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateProduct(
            @PathVariable Long productId,
            @RequestParam @NotBlank(message = "Tên sản phẩm không được để trống!") String name,
            @RequestParam(required = false) String description,
            @RequestParam @NotNull(message = "Giá không được để trống!") @Min(value = 0, message = "Giá không được nhỏ hơn 0.") Long price,
            @RequestParam @NotNull(message = "Danh mục không được để trống!") Long categoryId,
            @RequestParam @Min(value = 0, message = "Tồn kho không được nhỏ hơn 0.") int stockQuantity,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws AppException, IOException {
        return productService.updateProduct(productId, name, description, price, categoryId, stockQuantity, file);
    }

    /**
     * GET: Lấy tất cả sản phẩm với bộ lọc và phân trang
     */
    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @PageableDefault(size = 12, sort = "createdAt,desc") Pageable pageable) {
        Specification<Product> spec = productSpecification.filterProducts(categoryName, keyword, minPrice, maxPrice);
        return productService.getProducts(spec, pageable);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable Long productId) throws AppException {
        return productService.getProductById(productId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) throws AppException, IOException {
        productService.deleteProduct(productId);
    }
}
