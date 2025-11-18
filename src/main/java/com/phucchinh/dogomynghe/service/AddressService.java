package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.AddressCreationRequest;
import com.phucchinh.dogomynghe.dto.response.AddressResponse;
import com.phucchinh.dogomynghe.entity.Address;
import com.phucchinh.dogomynghe.entity.User;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.repository.AddressRepository;
import com.phucchinh.dogomynghe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {

    UserRepository userRepository;
    AddressRepository addressRepository;

    // --- PRIVATE HELPER METHODS ---

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Address mapToAddressEntity(AddressCreationRequest request, User user) {
        return Address.builder()
                .city(request.getCity())
                .commune(request.getCommune())
                .village(request.getVillage())
                .note(request.getNote())
                .user(user)
                .build();
    }

    AddressResponse mapToAddressResponse(Address address) {
        // Tạo chuỗi địa chỉ đầy đủ
        String fullAddress = String.format("%s, %s, %s, %s",
                address.getNote(),
                address.getVillage(),
                address.getCommune(),
                address.getCity());

        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .commune(address.getCommune())
                .village(address.getVillage())
                .note(address.getNote())
                .createdAt(address.getCreatedAt())
                .fullAddress(fullAddress) // Trả về chuỗi địa chỉ đầy đủ
                .build();
    }

    // --- PUBLIC SERVICE METHODS ---

    /**
     * 1. Lấy tất cả địa chỉ của User hiện tại
     */
    public List<AddressResponse> getAllAddresses(String username) {
        User user = findUserByUsername(username);

        // Lấy danh sách địa chỉ từ Entity User
        return user.getAddresses().stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    /**
     * 2. Thêm địa chỉ mới
     */
    public AddressResponse createAddress(String username, AddressCreationRequest request) {
        User user = findUserByUsername(username);

        Address newAddress = mapToAddressEntity(request, user);

        // Cập nhật quan hệ 1-N trong bộ nhớ (Memory)
        user.getAddresses().add(newAddress);

        // Lưu Address sẽ tự động Cascade thông qua OneToMany trong User
        Address savedAddress = addressRepository.save(newAddress);

        return mapToAddressResponse(savedAddress);
    }

    /**
     * 3. Cập nhật địa chỉ
     */
    public AddressResponse updateAddress(String username, Long addressId, AddressCreationRequest request) {
        User user = findUserByUsername(username);

        // Tìm địa chỉ và kiểm tra quyền sở hữu
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!existingAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ADDRESS_ACCESS_DENIED); // Cần định nghĩa mã lỗi này
        }

        // Cập nhật thông tin
        existingAddress.setCity(request.getCity());
        existingAddress.setCommune(request.getCommune());
        existingAddress.setVillage(request.getVillage());
        existingAddress.setNote(request.getNote());

        Address updatedAddress = addressRepository.save(existingAddress);
        return mapToAddressResponse(updatedAddress);
    }

    /**
     * 4. Xóa địa chỉ
     */
    public void deleteAddress(String username, Long addressId) {
        User user = findUserByUsername(username);

        // Tìm địa chỉ và kiểm tra quyền sở hữu
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!existingAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ADDRESS_ACCESS_DENIED);
        }

        // Xóa địa chỉ (orphanRemoval trong User Entity sẽ giúp giữ đồng bộ)
        addressRepository.delete(existingAddress);
    }
}