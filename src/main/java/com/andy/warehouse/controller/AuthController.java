package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.LoginRequest;
import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出登录成功");
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refresh() {
        return Result.success("Token刷新成功");
    }
}
