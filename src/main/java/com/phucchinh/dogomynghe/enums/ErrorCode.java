package com.phucchinh.dogomynghe.enums;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Success code
    SUCCESS(1000, "Success", HttpStatus.OK),

    // User/Auth Errors (2xxx)
    USER_NOT_FOUND(2001, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(2002, "Thông tin đăng nhập không hợp lệ.", HttpStatus.UNAUTHORIZED),
    EMAIL_EXISTED(2003, "Email đã được sử dụng.", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(2004, "Số điện thoại đã được sử dụng.", HttpStatus.BAD_REQUEST),

    // Internal Server Error
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    CATEGORY_NAME_EXISTED(3001,"Danh mục đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    CATEGORY_NOT_FOUND(3002, "Danh mục ko tồn tại" , HttpStatus.NOT_FOUND),
    UPLOAD_FAILED(4444,"Up ảnh thất bại" , HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_FOUND(4001, "Sản phẩm ko tồn tại", HttpStatus.NOT_FOUND),

    REFRESH_TOKEN_EXPIRED(5001,"Token này đã hết hạn rồi" ,HttpStatus.BAD_REQUEST ),

    REFRESH_TOKEN_NOT_FOUND(5002,"ko tìm thấy refresh token" , HttpStatus.NOT_FOUND),

    INVALID_QUANTITY(6001,"Số lượng phải >=0" , HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(6002, "Ko tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(6003, "Ko tìm thấy sản phẩm trong giỏ", HttpStatus.NOT_FOUND),

    CART_IS_EMPTY(7001, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(7002, "Ko thấy đơn hàng", HttpStatus.NOT_FOUND),

    ADDRESS_NOT_FOUND(8001, "Địa chỉ ko tìm thay", HttpStatus.NOT_FOUND),
    ADDRESS_ACCESS_DENIED(8002, "Địa chỉ bị từ chối", HttpStatus.BAD_REQUEST),

    ORDER_CANNOT_BE_UPDATED(9001, "Ko thể cập nhật", HttpStatus.BAD_REQUEST),

    ORDER_CANCEL_NOT_ALLOWED(7003, "Không thể hủy đơn hàng ở trạng thái này (chỉ được hủy khi 'Đang chờ xử lý').", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_ALLOWED(8888, "Ko đc phép review", HttpStatus.BAD_REQUEST),

    OUT_OF_STOCK(6004, "Sản phẩm này đã hết hàng hoặc không đủ số lượng.", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXISTED(9002, "Bạn đã đánh giá sản phẩm này rồi.", HttpStatus.BAD_REQUEST),
    NEWS_EXISTED(2005, "Tin tức đã tồn tại.", HttpStatus.BAD_REQUEST),
    NEWS_NOT_EXISTED(2006, "Tin tức không tồn tại.", HttpStatus.NOT_FOUND),
    IMAGE_UPLOAD_FAILED(2007, "Tải ảnh thất bại.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}