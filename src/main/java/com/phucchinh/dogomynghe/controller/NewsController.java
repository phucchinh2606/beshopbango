package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.request.NewsRequest;
import com.phucchinh.dogomynghe.dto.response.NewsResponse;
import com.phucchinh.dogomynghe.exception.AppException;
import com.phucchinh.dogomynghe.service.NewsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {

    NewsService newsService;

    // --- ADMIN CHỈ CÓ QUYỀN THỰC HIỆN CÁC THAO TÁC CRUD NÀY ---

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse createNews(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(value = "image", required = false) MultipartFile image) throws AppException {
        NewsRequest request = NewsRequest.builder()
                .title(title)
                .content(content)
                .build();
        return newsService.createNews(request, image);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PutMapping("/{newsId}")
    public NewsResponse updateNews(
            @PathVariable Long newsId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(value = "image", required = false) MultipartFile image) throws AppException {
        NewsRequest request = NewsRequest.builder()
                .title(title)
                .content(content)
                .build();
        return newsService.updateNews(newsId, request, image);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @DeleteMapping("/{newsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable Long newsId) throws AppException {
        newsService.deleteNews(newsId);
    }

    // --- CÁC THAO TÁC READ CÓ THỂ CÔNG KHAI HOẶC CHO CẢ USER/ADMIN ---

    @GetMapping
    public List<NewsResponse> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{newsId}")
    public NewsResponse getNewsById(@PathVariable Long newsId) throws AppException {
        return newsService.getNewsById(newsId);
    }
}

