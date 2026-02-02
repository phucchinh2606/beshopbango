package com.phucchinh.dogomynghe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsRequest {
    @NotBlank(message = "Title is required!")
    String title;
    @NotBlank(message = "Content is required!")
    String content;
    String imageUrl;
}

