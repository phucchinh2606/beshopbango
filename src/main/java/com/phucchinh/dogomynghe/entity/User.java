package com.phucchinh.dogomynghe.entity;

import com.phucchinh.dogomynghe.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    @NotBlank(message = "Username is required!")
    String username;

    @NotBlank(message = "Password is required!")
    String password;

    @Column(unique = true)
    @NotBlank(message = "Email is required!")
    String email;

    @Column(unique = true)
    @NotBlank(message = "Phone number is required!")
    String phoneNumber;

    @NotBlank(message = "DateOfBirth number is required!")
    String  dateOfBirth;

    @NotBlank(message = "Gender number is required!")
    String gender;

    String imgAvatar;

    String addresses;

    @Enumerated(EnumType.STRING)
    UserRole userRole = UserRole.ROLE_USER;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    List<Address> addresses;

    // Mối quan hệ 1-1 với Cart
    // mappedBy trỏ đến trường "user" trong lớp Cart
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Cart cart; // Mỗi User chỉ có 1 giỏ hàng hoạt động

    //Mối quan hệ 1-N với Order (Lịch sử đặt hàng)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Order> orders; // Lịch sử các đơn hàng đã đặt của người dùng


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về danh sách quyền hạn (role)
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
