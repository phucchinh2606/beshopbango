package com.phucchinh.dogomynghe.dto.request;

import com.phucchinh.dogomynghe.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRoleUpdateRequest {
    UserRole newRole;
}