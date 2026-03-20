package com.warehouse.management.service;

import com.warehouse.management.entity.Permission;
import com.warehouse.management.entity.Role;
import com.warehouse.management.entity.User;
import com.warehouse.management.model.LoginRequest;
import com.warehouse.management.model.LoginResponse;
import com.warehouse.management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        User user = userService.findByUsername(request.getUsername()).orElseThrow();

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                roles.add(role.getCode());
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        permissions.add(permission.getCode());
                    }
                }
            }
        }

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
