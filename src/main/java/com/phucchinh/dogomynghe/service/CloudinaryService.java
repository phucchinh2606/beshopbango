package com.phucchinh.dogomynghe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary; // Sử dụng Bean đã tạo trong CloudinaryConfig

    public String uploadFile(MultipartFile file, String folder) throws IOException {

        // Thiết lập tham số upload, bao gồm thư mục (folder)
        Map uploadOptions = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto" // Tự động nhận diện loại tài nguyên (image/video)
        );

        try {
            // Upload file và nhận về Map kết quả
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            // Trả về URL an toàn (HTTPS) của ảnh đã upload
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return; // Không làm gì nếu URL rỗng
        }

        // 1. Trích xuất Public ID từ URL
        String publicId = extractPublicIdFromUrl(fileUrl);

        if (publicId == null || publicId.isEmpty()) {
            log.warn("Could not extract Public ID from URL: {}", fileUrl);
            return;
        }

        try {
            // 2. Xóa file khỏi Cloudinary
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // Kiểm tra kết quả
            if ("not found".equals(result.get("result"))) {
                log.warn("Cloudinary delete failed: File with Public ID {} not found.", publicId);
            } else {
                log.info("Cloudinary delete successful for Public ID: {}", publicId);
            }
        } catch (IOException e) {
            log.error("Error deleting file {} from Cloudinary: {}", publicId, e.getMessage());
            throw new IOException("Failed to delete file from Cloudinary", e);
        }
    }

    /**
     * Phương thức tiện ích để trích xuất Public ID từ URL của Cloudinary.
     * Ví dụ: Từ "https://res.cloudinary.com/deurw9jqh/image/upload/v123456789/products/bgjgtvmwszzopmj2bzm5.jpg"
     * Trả về: "products/bgjgtvmwszzopmj2bzm5"
     */
    private String extractPublicIdFromUrl(String url) {
        // Regex để tìm phần Public ID (sau version number 'v.../' và trước extension '.jpg')
        // Pattern tìm kiếm: /upload/v\d+/(<public_id>)\.(<extension>)
        Pattern pattern = Pattern.compile("/v\\d+/(.+?)\\.\\w+$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find() && matcher.groupCount() >= 1) {
            // Group 1 chứa Public ID (ví dụ: products/bgjgtvmwszzopmj2bzm5)
            return matcher.group(1);
        }
        return null;
    }
}