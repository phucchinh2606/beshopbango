package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.OrderCreationRequest;
import com.phucchinh.dogomynghe.dto.response.*;
import com.phucchinh.dogomynghe.entity.*;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.enums.OrderStatus;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

    // Dependencies (Từ các file bạn đã cung cấp)
    UserRepository userRepository;
    CartRepository cartRepository;
    OrderRepository orderRepository;
    AddressRepository addressRepository;
    CartItemRepository cartItemRepository; // Cần để xóa CartItem
    ProductRepository productRepository;


    // Mappers (Giả sử bạn có AddressService hoặc mappers riêng)
    AddressService addressService; // Dùng để map Address -> AddressResponse

    // =========================================================
    //               1. TẠO ĐƠN HÀNG (CHECKOUT)
    // =========================================================
    @Transactional
    public OrderResponse createOrderFromCart(String username, OrderCreationRequest request) {

        // 1. Lấy thông tin User và Địa chỉ (Giữ nguyên)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        Address shippingAddress = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ADDRESS_ACCESS_DENIED);
        }

        // 2. Lấy Giỏ hàng (Cart)
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        // 3. ⭐️ LẤY CÁC SẢN PHẨM ĐÃ CHỌN (Logic mới)
        List<Long> requestedItemIds = request.getCartItemIds();

        // Dùng phương thức Repository mới để lấy CHÍNH XÁC các item hợp lệ
        List<CartItem> itemsToCheckout = cartItemRepository.findAllById(requestedItemIds).stream()
                .filter(item -> item.getCart().getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());

        // 4. Xác thực các sản phẩm đã chọn
        if (itemsToCheckout.isEmpty()) {
            // Nếu không có sản phẩm nào hợp lệ (hoặc không chọn gì)
            throw new AppException(ErrorCode.CART_IS_EMPTY);
        }

        if (itemsToCheckout.size() != requestedItemIds.size()) {
            // Cảnh báo: Người dùng có thể đã gửi ID rác hoặc ID của giỏ hàng khác
            log.warn("User {} requested {} items but only {} were valid/found in their cart.",
                    user.getId(), requestedItemIds.size(), itemsToCheckout.size());
        }

        // --- 👇 LOGIC KIỂM TRA & TRỪ KHO ---
        for (CartItem cartItem : itemsToCheckout) {
            Product product = cartItem.getProduct();
            int requestedQty = cartItem.getQuantity();

            // 1. Kiểm tra đủ hàng không
            if (product.getStockQuantity() < requestedQty) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
                // Bạn có thể throw lỗi chi tiết hơn: "Sản phẩm " + product.getName() + " không đủ hàng."
            }

            // 2. Trừ kho
            product.setStockQuantity(product.getStockQuantity() - requestedQty);

            // 3. Lưu lại Product (JPA sẽ tự update khi commit transaction)
            // productRepository.save(product); // Không cần thiết nếu đang trong @Transactional, nhưng gọi cho chắc cũng được
        }

        // 5. Tính tổng tiền (CHỈ TÍNH DỰA TRÊN itemsToCheckout)
        Long totalAmount = itemsToCheckout.stream()
                .mapToLong(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();

        // 6. Tạo Order Entity
        Order newOrder = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .customerNote(request.getCustomerNote()) // Thêm Ghi chú
                .paymentMethod(request.getPaymentMethod()) // Thêm phương thức thanh toán
                .build();

        // 7. Chuyển CartItem (ĐÃ CHỌN) sang OrderItem
        List<OrderItem> orderItems = itemsToCheckout.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    return OrderItem.builder()
                            .order(newOrder)
                            .product(product)
                            .priceAtPurchase(product.getPrice())
                            .quantity(cartItem.getQuantity())
                            .productName(product.getName()) // Lưu tên sản phẩm
                            .build();
                })
                .collect(Collectors.toList());

        newOrder.setItems(orderItems);

        // 8. Lưu Order (OrderItem sẽ tự lưu)
        Order savedOrder = orderRepository.save(newOrder);

        // 9. Dọn dẹp Giỏ hàng (CHỈ XÓA NHỮNG SẢN PHẨM ĐÃ MUA)
        // Chúng ta xóa trực tiếp các CartItem đã được thanh toán
        cartItemRepository.deleteAll(itemsToCheckout);

        // 10. Trả về Response DTO
        return mapToOrderResponse(savedOrder);
    }

    // =========================================================
    //               2. LẤY LỊCH SỬ ĐƠN HÀNG
    // =========================================================
    public List<OrderResponse> getOrderHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // Dùng phương thức từ OrderRepository bạn cung cấp
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    //               3. XEM CHI TIẾT ĐƠN HÀNG
    // =========================================================
    public OrderResponse getOrderDetail(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // Dùng phương thức từ OrderRepository bạn cung cấp (đã bổ sung)
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kích hoạt LAZY loading (nếu cần)
        order.getItems().size();

        return mapToOrderResponse(order);
    }

    /**
     * [ADMIN] Lấy tất cả đơn hàng (có phân trang).
     */
    public Page<OrderResponse> adminGetAllOrders(Pageable pageable) {
        // Dùng phương thức findAll(Pageable) của JpaRepository
        Page<Order> orderPage = orderRepository.findAll(pageable);
        
        System.out.println("adminGetAllOrders - Total orders: " + orderPage.getTotalElements());
        System.out.println("adminGetAllOrders - Page size: " + pageable.getPageSize());
        System.out.println("adminGetAllOrders - Page number: " + pageable.getPageNumber());
        orderPage.getContent().forEach(order -> {
            System.out.println("Order ID: " + order.getId() + ", Status: " + order.getStatus());
        });

        // Dùng .map() của Page để chuyển đổi Order Entity sang OrderResponse DTO
        return orderPage.map(this::mapToOrderResponse);
    }

    /**
     * [ADMIN] Lấy các đơn hàng theo trạng thái (có phân trang).
     */
    public Page<OrderResponse> adminGetOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        return orderPage.map(this::mapToOrderResponse);
    }

    /**
     * [ADMIN] Xem chi tiết một đơn hàng bất kỳ (không cần kiểm tra ownership).
     */
    public OrderResponse adminGetOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kích hoạt LAZY loading (nếu cần, dù findById thường là EAGER)
        order.getItems().size();

        return mapToOrderResponse(order);
    }

    /**
     * [ADMIN] Cập nhật trạng thái đơn hàng.
     */
    @Transactional
    public OrderResponse adminUpdateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Logic nghiệp vụ (ví dụ):
        // 1. Không thể cập nhật đơn đã Giao (DELIVERED) hoặc đã Hủy (CANCELLED)
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_UPDATED); // Cần tạo mã lỗi này
        }

        // 2. Nếu chuyển sang Giao hàng (SHIPPING), kiểm tra tồn kho (Logic nâng cao)
        // ... (Bỏ qua trong ví dụ này)

        // 3. Cập nhật trạng thái
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // 4. (Nâng cao) Gửi email/thông báo cho khách hàng về trạng thái mới
        // ...

        return mapToOrderResponse(savedOrder);
    }

    // =========================================================
    //               CHỨC NĂNG MỚI: HỦY ĐƠN (USER)
    // =========================================================

    /**
     * [USER] Hủy một đơn hàng.
     * @param username Người dùng hiện tại
     * @param orderId ID của đơn hàng cần hủy
     * @return OrderResponse của đơn hàng đã được cập nhật trạng thái
     */
    @Transactional
    public OrderResponse userCancelOrder(String username, Long orderId) {

        // 1. Lấy User (để xác thực)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // 2. Tìm đơn hàng VÀ kiểm tra quyền sở hữu
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // 3. ⭐️ LOGIC NGHIỆP VỤ: Chỉ cho phép hủy khi đang PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }

        // 1. Cập nhật trạng thái
        order.setStatus(OrderStatus.CANCELLED);

        // 2. Hoàn trả số lượng về kho
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            // Cộng lại số lượng đã mua
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // 6. Lưu và trả về
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    // =========================================================
    //               CÁC PHƯƠNG THỨC MAPPER (HELPER)
    // =========================================================

    // ⭐️ THÊM MAPPER MỚI CHO USER (HELPER)
    private UserMinimalResponse mapToUserMinimalResponse(User user) {
        return UserMinimalResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    // ⭐️ CẬP NHẬT mapToOrderResponse
    OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        AddressResponse addressResponse = addressService.mapToAddressResponse(order.getShippingAddress());

        // ⭐️ BỔ SUNG: Ánh xạ User sang UserMinimalResponse
        UserMinimalResponse userResponse = mapToUserMinimalResponse(order.getUser());

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .shippingAddress(addressResponse)
                .items(itemResponses)
                .user(userResponse) // ⭐️ GÁN DỮ LIỆU USER VÀO RESPONSE
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paidAt(order.getPaidAt())
                .build();
    }

    OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .product(mapToProductMinimalResponse(item.getProduct()))
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subTotal(item.getQuantity() * item.getPriceAtPurchase())
                .build();
    }

    ProductMinimalResponse mapToProductMinimalResponse(Product product) {
        return ProductMinimalResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice()) // Giá hiện tại
                .build();
    }
}