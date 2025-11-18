package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.CartItemRequest;
import com.phucchinh.dogomynghe.dto.response.CartResponse;
import com.phucchinh.dogomynghe.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    /**
     * GET /api/v1/user/cart : Xem chi tiết giỏ hàng
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    /**
     * POST /api/v1/user/cart/add : Thêm sản phẩm vào giỏ hàng
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(@RequestBody @Valid CartItemRequest request) {
        CartResponse updatedCart = cartService.addToCart(request);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    /**
     * PATCH /api/v1/user/cart/items/{cartItemId}?quantity=X : Cập nhật số lượng
     */
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {

        CartResponse updatedCart = cartService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * DELETE /api/v1/user/cart/items/{cartItemId} : Xóa 1 CartItem
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long cartItemId) {
        CartResponse updatedCart = cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * DELETE /api/v1/user/cart/clear : Xóa toàn bộ giỏ hàng
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart() {
        CartResponse clearedCart = cartService.clearCart();
        return ResponseEntity.ok(clearedCart);
    }
}