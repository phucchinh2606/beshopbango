package com.phucchinh.dogomynghe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", folder));
        return (String) uploadResult.get("url");
    }

    public void deleteFile(String imageUrl) throws IOException {
        // Extract public ID from the image URL
        String publicId = extractPublicIdFromUrl(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        // Assuming Cloudinary URL format like: http://res.cloudinary.com/<cloud_name>/image/upload/<public_id>.<extension>
        // Or with version: http://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<public_id>.<extension>
        int startIndex = imageUrl.indexOf("/upload/") + "/upload/".length();
        int endIndex = imageUrl.lastIndexOf('.');
        String publicIdWithVersion = imageUrl.substring(startIndex, endIndex);
        // Remove version number if present (e.g., v1234567890/folder/image_name -> folder/image_name)
        if (publicIdWithVersion.matches("v\\d+/.+")) {
            return publicIdWithVersion.substring(publicIdWithVersion.indexOf('/') + 1);
        }
        return publicIdWithVersion;
    }
}

