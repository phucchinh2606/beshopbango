package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.CartItemRequest;
import com.phucchinh.dogomynghe.dto.response.CartItemResponse;
import com.phucchinh.dogomynghe.dto.response.CartResponse;
import com.phucchinh.dogomynghe.dto.response.ProductMinimalResponse;
import com.phucchinh.dogomynghe.entity.Cart;
import com.phucchinh.dogomynghe.entity.CartItem;
import com.phucchinh.dogomynghe.entity.Product;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.ErrorCode; // Giả định
import com.phucchinh.dogomynghe.exception.AppException; // Giả định
import com.phucchinh.dogomynghe.repository.CartItemRepository;
import com.phucchinh.dogomynghe.repository.CartRepository;
import com.phucchinh.dogomynghe.repository.ProductRepository;
import com.phucchinh.dogomynghe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    private User getCurrentUser() {
        // Lấy username từ Security Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Cart getOrCreateCart(User user) {
        // Tìm giỏ hàng hiện tại, nếu chưa có thì tạo mới
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        Product product = item.getProduct();
        Long subtotal = product.getPrice() * item.getQuantity();

        ProductMinimalResponse productResponse = ProductMinimalResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();

        return CartItemResponse.builder()
                .id(item.getId())
                .product(productResponse)
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        Long totalCartPrice = itemResponses.stream()
                .mapToLong(CartItemResponse::getSubtotal)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalCartPrice(totalCartPrice)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    /**
     * Lấy thông tin giỏ hàng của người dùng hiện tại
     */
    public CartResponse getCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user); // Đảm bảo luôn có giỏ hàng

        return mapToCartResponse(cart);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng (cộng dồn số lượng nếu đã tồn tại)
     */
    @Transactional
    public CartResponse addToCart(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            // Cập nhật số lượng
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Tạo CartItem mới
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        // Lấy lại Cart để đảm bảo dữ liệu mới nhất được ánh xạ sang Response
        Cart updatedCart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapToCartResponse(updatedCart);
    }

    /**
     * Cập nhật số lượng của một CartItem đã có
     */
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, int newQuantity) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        if (newQuantity <= 0) {
            // Nếu số lượng là 0 hoặc âm, coi như xóa item đó
            return removeCartItem(cartItemId);
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        // Lấy lại Cart để ánh xạ
        Cart updatedCart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapToCartResponse(updatedCart);
    }

    /**
     * Xóa một CartItem khỏi giỏ hàng
     */
    @Transactional
    public CartResponse removeCartItem(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Optional<CartItem> itemToRemoveOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (itemToRemoveOpt.isEmpty()) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        CartItem itemToRemove = itemToRemoveOpt.get();

        // Xóa CartItem khỏi danh sách của Cart và Repository
        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        // Lấy lại Cart để ánh xạ
        Cart updatedCart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapToCartResponse(updatedCart);
    }

    /**
     * Xóa toàn bộ sản phẩm trong giỏ hàng
     */
    @Transactional
    public CartResponse clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        Cart clearedCart = cartRepository.save(cart); // Lưu lại Cart rỗng

        return mapToCartResponse(clearedCart);
    }
}