package com.andy.warehouse.service.impl;

import com.andy.warehouse.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, String> refreshTokens = new ConcurrentHashMap<>();

    @Override
    public void addToBlacklist(String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklist.put(token, System.currentTimeMillis() + 86400000L);
            log.info("Token added to blacklist");
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Long expiry = tokenBlacklist.get(token);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiry) {
            tokenBlacklist.remove(token);
            return false;
        }
        return true;
    }

    @Override
    public void storeRefreshToken(String username, String token) {
        if (username != null && token != null) {
            refreshTokens.put(username, token);
            log.info("Refresh token stored for user: {}", username);
        }
    }

    @Override
    public String getRefreshToken(String username) {
        return refreshTokens.get(username);
    }

    @Override
    public void removeRefreshToken(String username) {
        refreshTokens.remove(username);
        log.info("Refresh token removed for user: {}", username);
    }

    @Override
    public boolean validateRefreshToken(String username, String token) {
        String storedToken = refreshTokens.get(username);
        return storedToken != null && storedToken.equals(token);
    }

    @Override
    public Set<String> getAllUsersWithRefreshToken() {
        return refreshTokens.keySet();
    }
}
