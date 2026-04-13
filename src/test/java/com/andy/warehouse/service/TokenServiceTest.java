package com.andy.warehouse.service;

import com.andy.warehouse.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
    }

    @Test
    void testAddToBlacklist() {
        String token = "test-token-123";
        
        assertFalse(tokenService.isBlacklisted(token));
        
        tokenService.addToBlacklist(token);
        
        assertTrue(tokenService.isBlacklisted(token));
    }

    @Test
    void testIsBlacklistedWithNullToken() {
        assertFalse(tokenService.isBlacklisted(null));
        assertFalse(tokenService.isBlacklisted(""));
    }

    @Test
    void testStoreAndGetRefreshToken() {
        String username = "testUser";
        String token = "refresh-token-123";
        
        tokenService.storeRefreshToken(username, token);
        
        assertEquals(token, tokenService.getRefreshToken(username));
    }

    @Test
    void testRemoveRefreshToken() {
        String username = "testUser";
        String token = "refresh-token-123";
        
        tokenService.storeRefreshToken(username, token);
        assertNotNull(tokenService.getRefreshToken(username));
        
        tokenService.removeRefreshToken(username);
        assertNull(tokenService.getRefreshToken(username));
    }

    @Test
    void testValidateRefreshToken() {
        String username = "testUser";
        String token = "refresh-token-123";
        String wrongToken = "wrong-token-456";
        
        tokenService.storeRefreshToken(username, token);
        
        assertTrue(tokenService.validateRefreshToken(username, token));
        assertFalse(tokenService.validateRefreshToken(username, wrongToken));
        assertFalse(tokenService.validateRefreshToken("nonExistentUser", token));
    }

    @Test
    void testGetAllUsersWithRefreshToken() {
        String username1 = "user1";
        String username2 = "user2";
        String token1 = "token-1";
        String token2 = "token-2";
        
        tokenService.storeRefreshToken(username1, token1);
        tokenService.storeRefreshToken(username2, token2);
        
        Set<String> users = tokenService.getAllUsersWithRefreshToken();
        
        assertEquals(2, users.size());
        assertTrue(users.contains(username1));
        assertTrue(users.contains(username2));
    }

    @Test
    void testLogoutScenario() {
        String username = "testUser";
        String accessToken = "access-token-123";
        String refreshToken = "refresh-token-123";
        
        tokenService.storeRefreshToken(username, refreshToken);
        assertNotNull(tokenService.getRefreshToken(username));
        assertFalse(tokenService.isBlacklisted(accessToken));
        
        tokenService.addToBlacklist(accessToken);
        tokenService.removeRefreshToken(username);
        
        assertTrue(tokenService.isBlacklisted(accessToken));
        assertNull(tokenService.getRefreshToken(username));
    }

    @Test
    void testRefreshTokenScenario() {
        String username = "testUser";
        String oldRefreshToken = "old-refresh-token";
        String newRefreshToken = "new-refresh-token";
        
        tokenService.storeRefreshToken(username, oldRefreshToken);
        assertTrue(tokenService.validateRefreshToken(username, oldRefreshToken));
        
        tokenService.removeRefreshToken(username);
        tokenService.storeRefreshToken(username, newRefreshToken);
        
        assertFalse(tokenService.validateRefreshToken(username, oldRefreshToken));
        assertTrue(tokenService.validateRefreshToken(username, newRefreshToken));
    }
}
