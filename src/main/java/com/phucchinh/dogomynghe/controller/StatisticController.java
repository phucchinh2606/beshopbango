package com.phucchinh.dogomynghe.controller;

import com.phucchinh.dogomynghe.dto.response.stats.DashboardResponse;
import com.phucchinh.dogomynghe.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public DashboardResponse getDashboardStats() {
        return statisticService.getDashboardStats();
    }
}