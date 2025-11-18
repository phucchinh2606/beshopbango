// src/main/java/com/phucchinh/dogomynghe/entity/RefreshToken.java
package com.phucchinh.dogomynghe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Chuỗi Refresh Token, phải là duy nhất và có độ dài lớn
    @Column(unique = true, nullable = false, length = 1000)
    String token;

    // Thời gian hết hạn của Refresh Token (dùng Instant để lưu trữ thời gian UTC)
    Instant expiryDate;

    // Liên kết 1-1 với User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
}