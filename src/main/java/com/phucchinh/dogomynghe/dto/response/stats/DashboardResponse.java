package com.phucchinh.dogomynghe.dto.response.stats;

import lombok.*;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    Long totalRevenue;      // Tổng doanh thu
    Long totalOrders;       // Tổng số đơn
    Long cancelledOrders;   // Số đơn hủy
    Long totalCustomers;    // Tổng số khách
    Long totalProducts;     // Tổng số sản phẩm

    List<ChartData> monthlyRevenue;  // Biểu đồ doanh thu tháng
    List<ChartData> categoryRevenue; // Biểu đồ danh mục
    List<ProductStats> bestSellers;  // Top bán chạy
    List<ProductStats> lowStock;     // Cảnh báo kho
}