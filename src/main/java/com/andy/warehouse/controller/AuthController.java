package com.andy.warehouse.controller;

import com.andy.warehouse.dto.auth.LoginRequest;
import com.andy.warehouse.dto.auth.LoginResponse;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.repository.UserRepository;
import com.andy.warehouse.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenProvider.getExpiration());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        if (user.getOrganization() != null) {
            userInfo.setOrgName(user.getOrganization().getOrgName());
        }
        if (user.getDepartment() != null) {
            userInfo.setDeptName(user.getDepartment().getDeptName());
        }
        if (user.getRoles() != null) {
            userInfo.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
        }
        response.setUserInfo(userInfo);

        List<Permission> permissions = permissionRepository.findByUserId(user.getId());
        response.setPermissions(permissions.stream()
                .map(Permission::getPermissionCode)
                .distinct()
                .collect(Collectors.toList()));

        return Result.success("登录成功", response);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}
