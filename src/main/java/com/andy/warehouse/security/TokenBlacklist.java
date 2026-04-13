package com.andy.warehouse.security;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();
    private final JwtTokenUtil jwtTokenUtil;

    public TokenBlacklist(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        startCleanupThread();
    }

    public void addToBlacklist(String token) {
        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
        blacklist.put(token, expirationDate);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    public void removeExpiredTokens() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3600000);
                    removeExpiredTokens();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
}
