package com.phucchinh.dogomynghe.dto.request;

import com.phucchinh.dogomynghe.dto.response.AddressResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    Long id;

    String username;

    String password;

    String email;

    String  dateOfBirth;

    String gender;

    String phoneNumber;

    List<AddressResponse> addresses;

     MultipartFile imageFile;

}
