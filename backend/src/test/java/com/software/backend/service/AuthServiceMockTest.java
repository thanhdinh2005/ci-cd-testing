package com.software.backend.service;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.entity.User;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.repository.UserRepository;

import com.software.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceMockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    // ✅ Chỉ MỘT biến @InjectMocks
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
@DisplayName("Login - thông tin hợp lệ -> trả về LoginResponse với token")
void authenticate_validCredentials_returnsLoginResponse() {
    // Arrange
    LoginRequest request = new LoginRequest();
    request.setUsername("admin");
    request.setPassword("123456");

    User user = new User();
    user.setUsername("admin");
    user.setPassword("encoded-pass");

    when(userRepository.findByUsername("admin"))
            .thenReturn(Optional.of(user));
    when(passwordEncoder.matches("123456", "encoded-pass"))
            .thenReturn(true);
    when(jwtService.generateToken(user))
            .thenReturn("fake-token");

    // Act
LoginResponse response = authService.authenticate(request);

    // Assert
    assertNotNull(response);
    assertTrue(response.getIsSuccess());                // ✅ check success
    assertEquals("Success", response.getMessage()); // ✅ check message
    assertEquals("fake-token", response.getToken());    // ✅ check token
}


    @Test
    @DisplayName("Login - sai mật khẩu -> ném BadRequestException")
    void authenticate_wrongPassword_throwsBadRequest() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong-pass");

        User user = new User();
        user.setUsername("admin");
        user.setPassword("encoded-pass");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "encoded-pass"))
                .thenReturn(false);

        // Act + Assert
        assertThrows(BadRequestException.class,
                () -> authService.authenticate(request));

        verify(userRepository, times(1)).findByUsername("admin");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Login - username không tồn tại -> ném ResourceNotFoundException")
    void authenticate_userNotFound_throwsResourceNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("123456");

        when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> authService.authenticate(request));

        verify(userRepository, times(1)).findByUsername("ghost");
        verify(jwtService, never()).generateToken(any());
    }

   
}
