package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.LoginRequest;
import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.security.TokenBlacklist;
import com.andy.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlacklist tokenBlacklist;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            tokenBlacklist.addToBlacklist(token);
        }
        SecurityContextHolder.clearContext();
        return Result.success("退出登录成功");
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(HttpServletRequest request) {
        String oldToken = getTokenFromRequest(request);
        if (!StringUtils.hasText(oldToken)) {
            return Result.error("Token无效");
        }
        if (tokenBlacklist.isBlacklisted(oldToken)) {
            return Result.error("Token已失效，请重新登录");
        }
        String username = jwtTokenUtil.getUsernameFromToken(oldToken);
        tokenBlacklist.addToBlacklist(oldToken);
        String newToken = jwtTokenUtil.generateToken(username);
        return Result.success(LoginResponse.builder()
                .token(newToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build());
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
