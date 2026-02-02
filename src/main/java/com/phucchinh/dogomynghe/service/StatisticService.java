package com.phucchinh.dogomynghe.service;

import com.phucchinh.dogomynghe.dto.response.stats.*;
import com.phucchinh.dogomynghe.enums.OrderStatus;
import com.phucchinh.dogomynghe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    public DashboardResponse getDashboardStats() {
        // 1. Số liệu tổng quan
        Long revenue = orderRepository.sumTotalRevenue();
        Long totalOrders = orderRepository.count();
        Long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        Long totalCustomers = userRepository.count(); // Giả sử bạn có UserRepository
        Long totalProducts = productRepository.count();

        // 2. Biểu đồ doanh thu tháng (Lấy 6 tháng gần nhất)
        List<ChartData> monthlyRevenue = orderRepository.getMonthlyRevenue();
        if (monthlyRevenue.size() > 6) {
            monthlyRevenue = monthlyRevenue.subList(0, 6);
        }

        // 3. Biểu đồ danh mục
        List<ChartData> categoryRevenue = productRepository.getRevenueByCategory();

        // 4. Best Sellers (Lấy top 5)
        List<ProductStats> bestSellers = productRepository.findBestSellers(PageRequest.of(0, 5));

        // 5. Cảnh báo kho (Lấy sản phẩm còn <= 5)
        List<ProductStats> lowStock = productRepository.findByStockQuantityLessThanEqual(5).stream()
                .map(p -> ProductStats.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .stock((long) p.getStockQuantity())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalRevenue(revenue != null ? revenue : 0L)
                .totalOrders(totalOrders)
                .cancelledOrders(cancelledOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .monthlyRevenue(monthlyRevenue)
                .categoryRevenue(categoryRevenue)
                .bestSellers(bestSellers)
                .lowStock(lowStock)
                .build();
    }
}