package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Permission;
import com.warehouse.management.entity.Role;
import com.warehouse.management.entity.User;
import com.warehouse.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        Set<Role> roles = user.getRoles();
        if (roles != null) {
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
                Set<Permission> permissions = role.getPermissions();
                if (permissions != null) {
                    for (Permission permission : permissions) {
                        authorities.add(new SimpleGrantedAuthority(permission.getCode()));
                    }
                }
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
