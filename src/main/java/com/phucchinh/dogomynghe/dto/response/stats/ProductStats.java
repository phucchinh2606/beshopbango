package com.phucchinh.dogomynghe.dto.response.stats;

import lombok.*;

@Data
@Builder
@AllArgsConstructor // Cần thiết cho JPQL constructor expression
public class ProductStats {
    Long id;
    String name;
    Long sold;      // Số lượng đã bán
    Long stock;     // Số lượng tồn kho
    Long revenue;   // Doanh thu từ sản phẩm này (giá * số lượng bán)
    String img;
}