package com.software.backend.service.impl;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.entity.User;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.repository.UserRepository;
import com.software.backend.service.AuthService;
import com.software.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Override
    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed for user: {}. Password mismatch.", request.getUsername());
            throw new BadCredentialsException("Invalid username or password.");
        }

        log.info("User {} logged in successfully.", request.getUsername());

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .isSuccess(true)
                .message("Success")
                .token(token)
                .build();
    }
}

