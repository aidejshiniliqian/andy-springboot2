package com.andy.warehouse.controller;

import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.security.TokenBlacklist;
import com.andy.warehouse.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private TokenBlacklist tokenBlacklist;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userService, jwtTokenUtil, tokenBlacklist);
    }

    @Test
    void testLogout() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        
        when(tokenBlacklist.isBlacklisted(anyString())).thenReturn(false);
        
        var result = authController.logout(request);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenBlacklist, times(1)).addToBlacklist("test-token");
    }

    @Test
    void testRefreshToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer old-token");
        
        when(tokenBlacklist.isBlacklisted("old-token")).thenReturn(false);
        when(jwtTokenUtil.getUsernameFromToken("old-token")).thenReturn("testuser");
        when(jwtTokenUtil.generateToken("testuser")).thenReturn("new-token");
        
        var result = authController.refresh(request);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("new-token", result.getData().getToken());
        assertEquals("Bearer", result.getData().getTokenType());
        verify(tokenBlacklist, times(1)).addToBlacklist("old-token");
        verify(jwtTokenUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testRefreshTokenWithBlacklistedToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer blacklisted-token");
        
        when(tokenBlacklist.isBlacklisted("blacklisted-token")).thenReturn(true);
        
        var result = authController.refresh(request);
        
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("Token已失效，请重新登录", result.getMessage());
        verify(tokenBlacklist, never()).addToBlacklist(anyString());
        verify(jwtTokenUtil, never()).generateToken(anyString());
    }

    @Test
    void testRefreshTokenWithoutToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        var result = authController.refresh(request);
        
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("Token无效", result.getMessage());
    }
}
