package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.mapper.*;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final OrganizationMapper organizationMapper;
    private final DepartmentMapper departmentMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        loadUserRelations(user);
        String token = jwtTokenUtil.generateToken(request.getUsername());
        Set<Permission> permissions = getUserPermissions(user);
        return LoginResponse.builder()
                .token(token)
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
                .build();
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public LoginResponse refreshToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            throw new BusinessException("Token无效或已过期");
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        loadUserRelations(user);
        String newToken = jwtTokenUtil.generateToken(username);
        Set<Permission> permissions = getUserPermissions(user);
        return LoginResponse.builder()
                .token(newToken)
                .tokenType("Bearer")
                .expiresIn(expiration)
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
                .build();
    }

    private void loadUserRelations(User user) {
        if (user.getOrgId() != null) {
            user.setOrganization(organizationMapper.selectById(user.getOrgId()));
        }
        if (user.getDeptId() != null) {
            user.setDepartment(departmentMapper.selectById(user.getDeptId()));
        }
        List<Role> roles = userRoleMapper.findRolesByUserId(user.getId());
        user.setRoles(new HashSet<>(roles));
        for (Role role : roles) {
            List<Permission> permissions = rolePermissionMapper.findPermissionsByRoleId(role.getId());
            role.setPermissions(new HashSet<>(permissions));
        }
    }

    private Set<Permission> getUserPermissions(User user) {
        Set<Permission> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    permissions.addAll(role.getPermissions());
                }
            });
        }
        return permissions;
    }

    @Override
    @Transactional
    public User create(UserCreateRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());
        user.setOrgId(request.getOrgId());
        user.setDeptId(request.getDeptId());
        userMapper.insert(user);
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                Role role = roleMapper.selectById(roleId);
                if (role == null) {
                    throw new BusinessException("角色不存在: " + roleId);
                }
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public User update(UserUpdateRequest request) {
        User user = userMapper.selectById(request.getId());
        if (user == null || user.getDeleted()) {
            throw new BusinessException("用户不存在");
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getOrgId() != null) {
            user.setOrgId(request.getOrgId());
        }
        if (request.getDeptId() != null) {
            user.setDeptId(request.getDeptId());
        }
        userMapper.updateById(user);
        if (request.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            for (Long roleId : request.getRoleIds()) {
                Role role = roleMapper.selectById(roleId);
                if (role == null) {
                    throw new BusinessException("角色不存在: " + roleId);
                }
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setDeleted(true);
        userMapper.updateById(user);
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted()) {
            throw new BusinessException("用户不存在");
        }
        loadUserRelations(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        loadUserRelations(user);
        return user;
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, false);
        if (orgId != null) {
            wrapper.eq(User::getOrgId, orgId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getRealName, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        IPage<User> userPage = userMapper.selectPage(page, wrapper);
        for (User user : userPage.getRecords()) {
            loadUserRelations(user);
        }
        return (Page<User>) userPage;
    }

    @Override
    public List<User> getByRoleId(Long roleId) {
        return userMapper.findByRoleId(roleId);
    }

    @Override
    public List<User> getByDeptId(Long deptId) {
        return userMapper.findByDeptId(deptId);
    }

    @Override
    @Transactional
    public void changePassword(Long id, UserPasswordRequest request) {
        User user = getById(id);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        User user = getById(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long id, Set<Long> roleIds) {
        User user = getById(id);
        userRoleMapper.deleteByUserId(user.getId());
        for (Long roleId : roleIds) {
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                throw new BusinessException("角色不存在: " + roleId);
            }
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public Set<Role> getUserRoles(Long id) {
        User user = getById(id);
        return user.getRoles();
    }

    @Override
    public Set<Permission> getUserPermissions(Long id) {
        User user = getById(id);
        return getUserPermissions(user);
    }
}
