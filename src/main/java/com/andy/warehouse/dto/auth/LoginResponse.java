package com.andy.warehouse.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfo userInfo;
    private List<String> permissions;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String email;
        private String phone;
        private String avatar;
        private String orgName;
        private String deptName;
        private List<String> roles;
    }
}
