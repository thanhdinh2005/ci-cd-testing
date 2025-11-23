package com.software.backend.service.impl;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.entity.User;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.repository.UserRepository;
import com.software.backend.service.AuthService;
import com.software.backend.service.JwtService;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
public LoginResponse authenticate(LoginRequest request) throws BadRequestException {
    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BadRequestException("Sai mật khẩu");
    }

    String token = jwtService.generateToken(user);

    return LoginResponse.builder()
            .isSuccess(true)
            .message("Success")
            .token(token)
            .build();
}



   
}

