package com.software.backend.service;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.entity.User;
import com.software.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("TC01 - Đăng nhập thành công với thông tin hợp lệ")
    void testLoginSuccess() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPassword("123456");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(mockUser));

        LoginRequest request = new LoginRequest("admin", "123456");

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertTrue(response.getIsSuccess());
        assertEquals("Đăng nhập thành công", response.getMessage());
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("TC02 - Đăng nhập thất bại khi mật khẩu sai")
    void testLoginWrongPassword() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPassword("123456");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(mockUser));

        LoginRequest request = new LoginRequest("admin", "wrongpass");

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertFalse(response.getIsSuccess());
        assertEquals("Sai mật khẩu", response.getMessage());
    }

    @Test
    @DisplayName("TC03 - Đăng nhập thất bại khi username không tồn tại")
    void testLoginUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("ghost", "abc123");

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertFalse(response.getIsSuccess());
        assertEquals("Tài khoản không tồn tại", response.getMessage());
    }
}
