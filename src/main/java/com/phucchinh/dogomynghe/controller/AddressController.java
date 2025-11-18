package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.AddressCreationRequest;
import com.phucchinh.dogomynghe.dto.response.AddressResponse;
import com.phucchinh.dogomynghe.service.AddressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;

    /**
     * GET /api/v1/user/addresses
     * 1. Lấy tất cả địa chỉ của người dùng hiện tại
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<AddressResponse> getAllAddresses(Authentication authentication) {
        String username = authentication.getName();
        return addressService.getAllAddresses(username);
    }

    /**
     * POST /api/v1/user/addresses
     * 2. Thêm địa chỉ mới
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public AddressResponse createAddress(
            Authentication authentication,
            @RequestBody @Valid AddressCreationRequest request) {

        String username = authentication.getName();
        return addressService.createAddress(username, request);
    }

    /**
     * PUT /api/v1/user/addresses/{addressId}
     * 3. Cập nhật địa chỉ
     */
    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public AddressResponse updateAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @RequestBody @Valid AddressCreationRequest request) {

        String username = authentication.getName();
        return addressService.updateAddress(username, addressId, request);
    }

    /**
     * DELETE /api/v1/user/addresses/{addressId}
     * 4. Xóa địa chỉ
     */
    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    @PreAuthorize("hasRole('USER')")
    public void deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId) {

        String username = authentication.getName();
        addressService.deleteAddress(username, addressId);
    }
}