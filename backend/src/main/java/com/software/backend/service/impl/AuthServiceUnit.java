package com.software.backend.service.impl;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;

public class AuthServiceUnit {
    public LoginResponse authenticate(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return new LoginResponse(false, "Username không được để trống", null);
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return new LoginResponse(false, "Password không được để trống", null);
        }
        if (!request.getUsername().equals("testuser")) {
            return new LoginResponse(false, "Không tìm thấy người dùng", null);
        }
        if (!request.getPassword().equals("Test123")) {
            return new LoginResponse(false, "Sai mật khẩu", null);
        }
        return new LoginResponse(true, "Đăng nhập thành công", "fake-jwt-token");
    }
}
