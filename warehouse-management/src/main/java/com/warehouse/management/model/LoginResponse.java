package com.warehouse.management.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String realName;
    private List<String> roles;
    private List<String> permissions;
}
