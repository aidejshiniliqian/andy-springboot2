package com.andy.warehouse.controller;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.LoginRequest;
import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.dto.RefreshTokenRequest;
import com.andy.warehouse.dto.RefreshTokenResponse;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.service.TokenService;
import com.andy.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

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
            tokenService.addToBlacklist(token);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && StringUtils.hasText(authentication.getName())) {
            tokenService.removeRefreshToken(authentication.getName());
        }
        SecurityContextHolder.clearContext();
        return Result.success("退出登录成功", null);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("刷新令牌不能为空");
        }

        String username = null;
        for (String user : tokenService.getAllUsersWithRefreshToken()) {
            if (tokenService.validateRefreshToken(user, refreshToken)) {
                username = user;
                break;
            }
        }

        if (username == null) {
            throw new BusinessException("无效的刷新令牌");
        }

        String newToken = jwtTokenUtil.generateToken(username);
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");

        tokenService.removeRefreshToken(username);
        tokenService.storeRefreshToken(username, newRefreshToken);

        return Result.success(RefreshTokenResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(expiration)
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
