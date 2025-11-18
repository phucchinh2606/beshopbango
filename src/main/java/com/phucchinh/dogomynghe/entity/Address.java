package com.phucchinh.dogomynghe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "City is required!")
    String city;//thành pho

    @NotBlank(message = "Commune is required!")
    String commune;// xã, phuong

    @NotBlank(message = "Village is required!")
    String village;//thôn , tổ dân phố ,..

    String note;//dia chi cu the

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}
