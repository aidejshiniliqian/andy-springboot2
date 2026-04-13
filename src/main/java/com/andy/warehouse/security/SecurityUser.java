package com.andy.warehouse.security;

import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Integer status;
    private Set<Role> roles;
    private Set<Permission> permissions;

    public static SecurityUser fromUser(User user) {
        Set<Permission> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    permissions.addAll(role.getPermissions());
                }
            });
        }
        return SecurityUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .status(user.getStatus())
                .roles(user.getRoles())
                .permissions(permissions)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                    .collect(Collectors.toSet()));
        }
        if (permissions != null) {
            authorities.addAll(permissions.stream()
                    .filter(p -> p.getCode() != null)
                    .map(p -> new SimpleGrantedAuthority(p.getCode()))
                    .collect(Collectors.toSet()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
