package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegistrationRequest {

    // Username phải có độ dài tối thiểu 6 ký tự
    @NotBlank(message = "Username không được để trống!")
    @Size(min = 6, message = "Username phải có ít nhất 6 ký tự.")
    String username;

    // Password có độ dài tối thiểu 8 ký tự
    @NotBlank(message = "Password không được để trống!")
    @Size(min = 8, message = "Password phải có ít nhất 8 ký tự.")
    String password;

    // Email phải có định dạng hợp lệ
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ.")
    String email;

    // Số điện thoại phải có định dạng hợp lệ của Việt Nam
    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "Số điện thoại không hợp lệ.")
    String phoneNumber;
}