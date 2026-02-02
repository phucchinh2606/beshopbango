package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.request.NewsRequest;
import com.phucchinh.dogomynghe.dto.response.NewsResponse;
import com.phucchinh.dogomynghe.entity.News;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.enums.ErrorCode;
import com.phucchinh.dogomynghe.repository.NewsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NewsService {

    NewsRepository newsRepository;
    ImageUploadService imageUploadService;

    public NewsResponse createNews(NewsRequest request, MultipartFile image) throws AppException {
        if (newsRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new AppException(ErrorCode.NEWS_EXISTED);
        }

        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadFile(image, "news/");
                news.setImageUrl(imageUrl);
            } catch (IOException e) {
                log.error("Error uploading image for news", e);
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }
        News savedNews = newsRepository.save(news);
        return NewsResponse.builder()
                .id(savedNews.getId())
                .title(savedNews.getTitle())
                .content(savedNews.getContent())
                .imageUrl(savedNews.getImageUrl())
                .createdAt(savedNews.getCreatedAt())
                .updatedAt(savedNews.getUpdatedAt())
                .build();
    }

    public List<NewsResponse> getAllNews() {
        return newsRepository.findAll().stream()
                .map(news -> NewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .content(news.getContent())
                        .imageUrl(news.getImageUrl())
                        .createdAt(news.getCreatedAt())
                        .updatedAt(news.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public NewsResponse getNewsById(Long id) throws AppException {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_EXISTED));
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .imageUrl(news.getImageUrl())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }

    public NewsResponse updateNews(Long id, NewsRequest request, MultipartFile image) throws AppException {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_EXISTED));

        // Check if title is changed and if new title already exists for another news item
        if (!existingNews.getTitle().equals(request.getTitle())) {
            Optional<News> newsWithSameTitle = newsRepository.findByTitle(request.getTitle());
            if (newsWithSameTitle.isPresent() && !newsWithSameTitle.get().getId().equals(id)) {
                throw new AppException(ErrorCode.NEWS_EXISTED);
            }
        }

        existingNews.setTitle(request.getTitle());
        existingNews.setContent(request.getContent());

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadFile(image, "news/");
                existingNews.setImageUrl(imageUrl);
            } catch (IOException e) {
                log.error("Error uploading image for news", e);
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        } else if (request.getImageUrl() == null || request.getImageUrl().isEmpty()) {
            // If image is explicitly set to null/empty in request (and no new file uploaded), clear existing image
            // Optionally, delete old image from Cloudinary if existingNews.getImageUrl() is not null
            if (existingNews.getImageUrl() != null) {
                try {
                    imageUploadService.deleteFile(existingNews.getImageUrl());
                } catch (IOException e) {
                    log.warn("Failed to delete old image from Cloudinary: {}", e.getMessage());
                    // Continue without re-throwing, as the main update can proceed
                }
            }
            existingNews.setImageUrl(null);
        }

        News updatedNews = newsRepository.save(existingNews);
        return NewsResponse.builder()
                .id(updatedNews.getId())
                .title(updatedNews.getTitle())
                .content(updatedNews.getContent())
                .imageUrl(updatedNews.getImageUrl())
                .createdAt(updatedNews.getCreatedAt())
                .updatedAt(updatedNews.getUpdatedAt())
                .build();
    }

    public void deleteNews(Long id) throws AppException {
        if (!newsRepository.existsById(id)) {
            throw new AppException(ErrorCode.NEWS_NOT_EXISTED);
        }
        newsRepository.deleteById(id);
    }
}

