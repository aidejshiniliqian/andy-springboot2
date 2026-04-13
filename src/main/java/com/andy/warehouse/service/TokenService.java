package com.andy.warehouse.service;

import java.util.Set;

public interface TokenService {

    void addToBlacklist(String token);

    boolean isBlacklisted(String token);

    void storeRefreshToken(String username, String token);

    String getRefreshToken(String username);

    void removeRefreshToken(String username);

    boolean validateRefreshToken(String username, String token);

    Set<String> getAllUsersWithRefreshToken();
}
