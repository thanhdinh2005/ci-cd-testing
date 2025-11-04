package com.software.backend.service;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse authenticate(LoginRequest request);
}
