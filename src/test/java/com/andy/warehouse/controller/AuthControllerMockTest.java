package com.andy.warehouse.controller;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.LoginRequest;
import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.dto.RefreshTokenRequest;
import com.andy.warehouse.dto.RefreshTokenResponse;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.service.TokenService;
import com.andy.warehouse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerMockTest {

    private AuthController authController;
    private UserService userService;
    private TokenService tokenService;
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        tokenService = mock(TokenService.class);
        jwtTokenUtil = mock(JwtTokenUtil.class);
        authController = new AuthController(userService, tokenService, jwtTokenUtil);
        org.springframework.test.util.ReflectionTestUtils.setField(authController, "expiration", 86400000L);
    }

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        LoginResponse expectedResponse = LoginResponse.builder()
                .token("test-access-token")
                .refreshToken("test-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();

        when(userService.login(request)).thenReturn(expectedResponse);

        var result = authController.login(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(expectedResponse, result.getData());
        verify(userService).login(request);
    }

    @Test
    void testLogoutWithToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer test-access-token");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = authController.logout(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("退出登录成功", result.getMessage());
        verify(tokenService).addToBlacklist("test-access-token");
        verify(tokenService).removeRefreshToken("testUser");
    }

    @Test
    void testLogoutWithoutAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        var result = authController.logout(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tokenService, never()).addToBlacklist(any());
    }

    @Test
    void testRefreshTokenSuccess() {
        String oldRefreshToken = "old-refresh-token";
        String newAccessToken = "new-access-token";
        String username = "testUser";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(oldRefreshToken);

        when(tokenService.getAllUsersWithRefreshToken()).thenReturn(Set.of(username));
        when(tokenService.validateRefreshToken(username, oldRefreshToken)).thenReturn(true);
        when(jwtTokenUtil.generateToken(username)).thenReturn(newAccessToken);

        var result = authController.refresh(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(newAccessToken, result.getData().getToken());
        assertEquals("Bearer", result.getData().getTokenType());
        assertEquals(86400000L, result.getData().getExpiresIn());

        verify(tokenService).removeRefreshToken(username);
        verify(tokenService).storeRefreshToken(eq(username), anyString());
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        String invalidRefreshToken = "invalid-refresh-token";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(invalidRefreshToken);

        when(tokenService.getAllUsersWithRefreshToken()).thenReturn(Set.of());

        assertThrows(BusinessException.class, () -> authController.refresh(request));
    }

    @Test
    void testRefreshTokenWithNullToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(null);

        assertThrows(BusinessException.class, () -> authController.refresh(request));
    }

    @Test
    void testRefreshTokenWithEmptyToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("   ");

        when(tokenService.getAllUsersWithRefreshToken()).thenReturn(Set.of());

        assertThrows(BusinessException.class, () -> authController.refresh(request));
    }
}
