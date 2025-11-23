package com.software.backend.controller;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.ApiResponse;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) throws BadRequestException {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    

}
}
