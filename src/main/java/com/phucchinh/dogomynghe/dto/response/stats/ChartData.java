package com.phucchinh.dogomynghe.dto.response.stats;

import lombok.*;

@Data
@NoArgsConstructor // Cần thiết cho một số thư viện mapping
@Builder
public class ChartData {
    String name; // Tên (ví dụ: "Tháng 1", "2023")
    Long value;  // Giá trị

    // Constructor cho Monthly/Yearly Revenue
    public ChartData(Integer year, Integer month, Long value) {
        this.name = String.format("%02d/%d", month, year);
        this.value = value;
    }

    // Constructor cho Category Revenue (giữ nguyên nếu cần)
    public ChartData(String name, Long value) {
        this.name = name;
        this.value = value;
    }

}