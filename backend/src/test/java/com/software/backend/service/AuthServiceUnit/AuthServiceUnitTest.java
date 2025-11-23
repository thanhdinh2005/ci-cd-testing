package com.software.backend.service.AuthServiceUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.service.impl.AuthServiceUnit;

class AuthServiceTest {

    private AuthServiceUnit authServiceUnit;

    @BeforeEach
    void setUp() {
        authServiceUnit = new AuthServiceUnit();
    }

    @Test
    void testLoginSuccess() {
        AuthServiceUnit authService = new AuthServiceUnit();
        LoginRequest request = new LoginRequest("testuser", "Test123");

        LoginResponse res = authService.authenticate(request);

        assertTrue(res.getIsSuccess()); // nếu dùng Boolean
        assertEquals("Đăng nhập thành công", res.getMessage());
        assertEquals("fake-jwt-token", res.getToken());
    }


    @Test
    void testMissingUsername() {
        LoginRequest req = new LoginRequest(null, "Test123");
        LoginResponse res = authServiceUnit.authenticate(req);

        assertFalse(res.getIsSuccess());
        assertEquals("Username không được để trống", res.getMessage());
        assertNull(res.getToken());
    }

    @Test
    void testMissingPassword() {
        LoginRequest req = new LoginRequest("testuser", null);
        LoginResponse res = authServiceUnit.authenticate(req);

        assertFalse(res.getIsSuccess());
        assertEquals("Password không được để trống", res.getMessage());
        assertNull(res.getToken());

    }

    @Test
    void testWrongUsername() {
        LoginRequest req = new LoginRequest("wronguser", "Test123");
        LoginResponse res = authServiceUnit.authenticate(req);

        assertFalse(res.getIsSuccess());
        assertEquals("Không tìm thấy người dùng", res.getMessage());
        assertNull(res.getToken());
    }

    @Test
    void testWrongPassword() {
        LoginRequest req = new LoginRequest("testuser", "WrongPass");
        LoginResponse res = authServiceUnit.authenticate(req);

        assertFalse(res.getIsSuccess());
        assertEquals("Sai mật khẩu", res.getMessage());
        assertNull(res.getToken());
    }

    @Test
void testPasswordEmpty() {
    AuthServiceUnit authService = new AuthServiceUnit();
    LoginRequest req = new LoginRequest("testuser", "");
    LoginResponse res = authService.authenticate(req);

    assertFalse(res.getIsSuccess());
    assertEquals("Password không được để trống", res.getMessage());
    assertNull(res.getToken());
}

@Test
void testUsernameEmpty() {
    AuthServiceUnit authService = new AuthServiceUnit();
    LoginRequest req = new LoginRequest("", "Test123");
    LoginResponse res = authService.authenticate(req);

    assertFalse(res.getIsSuccess());
    assertEquals("Username không được để trống", res.getMessage());
    assertNull(res.getToken());
}


}