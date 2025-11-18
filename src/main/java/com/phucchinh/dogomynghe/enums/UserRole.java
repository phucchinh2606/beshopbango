package com.phucchinh.dogomynghe.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    ROLE_USER,ROLE_ADMIN;

    public SimpleGrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority(this.name());
    }
}
