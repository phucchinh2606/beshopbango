package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống!")
    String name;

    String description;
    
    // imageUrl sẽ được upload thông qua MultipartFile trong controller, 
    // không cần trong request body
}