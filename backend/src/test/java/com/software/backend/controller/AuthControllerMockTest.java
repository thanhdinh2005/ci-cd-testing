package com.software.backend.controller;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.exception.BadRequestException;
import com.software.backend.service.AuthService;
import com.software.backend.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // bỏ qua security filter
class AuthControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("API login - thông tin hợp lệ -> trả về ApiResponse<LoginResponse>")
    void login_validCredentials_returnsLoginResponse() throws Exception {
        // Arrange
        LoginResponse mockLoginResponse = new LoginResponse(true, "Success", "fake-token");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockLoginResponse);

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.isSuccess").value(true))
                .andExpect(jsonPath("$.data.message").value("Success"))
                .andExpect(jsonPath("$.data.token").value("fake-token"));
        
        verify(authService, times(1)).authenticate(any());
    }

    @Test
@DisplayName("API login - sai mật khẩu -> trả về 400 và ApiResponse.error")
void login_wrongPassword_returnsBadRequest() throws Exception {
    // Arrange

    when(authService.authenticate(any(LoginRequest.class)))
            .thenThrow(new BadRequestException("Sai mật khẩu"));

    // Act + Assert
    mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"wrongpass\"}"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Sai mật khẩu"))
            .andExpect(jsonPath("$.data").doesNotExist());

    verify(authService, times(1)).authenticate(any());
}

    @Test
    @DisplayName("API login - username không tồn tại -> trả về 404 và ApiResponse.error")
    void login_nonExistentUsername_returnsNotFound() throws Exception {
        // Arrange

        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new com.software.backend.exception.ResourceNotFoundException("User not found"));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content("{\"username\":\"nonexistent\",\"password\":\"123456\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).authenticate(any());
    }
}