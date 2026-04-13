package com.andy.warehouse.controller;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.LoginRequest;
import com.andy.warehouse.dto.LoginResponse;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出登录成功");
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username != null && jwtTokenUtil.validateToken(token)) {
                User user = userService.getByUsername(username);
                String newToken = jwtTokenUtil.generateToken(username);
                Set<Permission> permissions = userService.getUserPermissions(user.getId());
                return Result.success(LoginResponse.builder()
                        .token(newToken)
                        .tokenType("Bearer")
                        .expiresIn(86400000L)
                        .user(LoginResponse.UserInfo.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .realName(user.getRealName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .avatar(user.getAvatar())
                                .orgId(user.getOrgId())
                                .orgName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                                .deptId(user.getDeptId())
                                .deptName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                                .roles(user.getRoles().stream().map(Role::getCode).collect(Collectors.toList()))
                                .permissions(permissions.stream().map(Permission::getCode).collect(Collectors.toList()))
                                .build())
                        .build());
            }
        }
        throw new BusinessException("Token无效或已过期");
    }
}
