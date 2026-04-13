package com.andy.warehouse.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenBlacklistTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private TokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenBlacklist = new TokenBlacklist(jwtTokenUtil);
    }

    @Test
    void testAddToBlacklist() {
        String token = "test-token";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        
        when(jwtTokenUtil.getExpirationDateFromToken(token)).thenReturn(expirationDate);
        
        tokenBlacklist.addToBlacklist(token);
        
        assertTrue(tokenBlacklist.isBlacklisted(token));
        verify(jwtTokenUtil, times(1)).getExpirationDateFromToken(token);
    }

    @Test
    void testIsBlacklisted() {
        String token = "test-token";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        
        when(jwtTokenUtil.getExpirationDateFromToken(token)).thenReturn(expirationDate);
        
        assertFalse(tokenBlacklist.isBlacklisted(token));
        
        tokenBlacklist.addToBlacklist(token);
        
        assertTrue(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    void testRemoveExpiredTokens() {
        String expiredToken = "expired-token";
        String validToken = "valid-token";
        
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        Date validDate = new Date(System.currentTimeMillis() + 3600000);
        
        when(jwtTokenUtil.getExpirationDateFromToken(expiredToken)).thenReturn(expiredDate);
        when(jwtTokenUtil.getExpirationDateFromToken(validToken)).thenReturn(validDate);
        
        tokenBlacklist.addToBlacklist(expiredToken);
        tokenBlacklist.addToBlacklist(validToken);
        
        assertTrue(tokenBlacklist.isBlacklisted(expiredToken));
        assertTrue(tokenBlacklist.isBlacklisted(validToken));
        
        tokenBlacklist.removeExpiredTokens();
        
        assertFalse(tokenBlacklist.isBlacklisted(expiredToken));
        assertTrue(tokenBlacklist.isBlacklisted(validToken));
    }
}
