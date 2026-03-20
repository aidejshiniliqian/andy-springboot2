package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.*;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.repository.DepartmentRepository;
import com.andy.warehouse.repository.OrganizationRepository;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.repository.RoleRepository;
import com.andy.warehouse.repository.UserRepository;
import com.andy.warehouse.security.JwtTokenUtil;
import com.andy.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsernameAndDeletedFalse(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
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
                        .orgId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                        .orgName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                        .deptId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                        .deptName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                        .roles(user.getRoles().stream().map(Role::getCode).collect(Collectors.toList()))
                        .permissions(permissions.stream().map(Permission::getCode).collect(Collectors.toList()))
                        .build())
                .build();
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
        if (userRepository.existsByUsername(request.getUsername())) {
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
        if (request.getOrgId() != null) {
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new BusinessException("组织机构不存在"));
            user.setOrganization(org);
        }
        if (request.getDeptId() != null) {
            Department dept = departmentRepository.findById(request.getDeptId())
                    .orElseThrow(() -> new BusinessException("部门不存在"));
            user.setDepartment(dept);
        }
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new BusinessException("角色不存在: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getDeleted()) {
            throw new BusinessException("用户已被删除");
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
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new BusinessException("组织机构不存在"));
            user.setOrganization(org);
        }
        if (request.getDeptId() != null) {
            Department dept = departmentRepository.findById(request.getDeptId())
                    .orElseThrow(() -> new BusinessException("部门不存在"));
            user.setDepartment(dept);
        }
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new BusinessException("角色不存在: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    public Page<User> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<User> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            if (orgId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("organization").get("id"), orgId));
            }
            if (StringUtils.hasText(keyword)) {
                var namePredicate = cb.like(root.get("username"), "%" + keyword + "%");
                var realNamePredicate = cb.like(root.get("realName"), "%" + keyword + "%");
                predicates = cb.and(predicates, cb.or(namePredicate, realNamePredicate));
            }
            return predicates;
        };
        return userRepository.findAll(spec, pageable);
    }

    @Override
    public List<User> getByRoleId(Long roleId) {
        return userRepository.findByRoleId(roleId);
    }

    @Override
    public List<User> getByDeptId(Long deptId) {
        return userRepository.findByDeptId(deptId);
    }

    @Override
    @Transactional
    public void changePassword(Long id, UserPasswordRequest request) {
        User user = getById(id);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        User user = getById(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long id, Set<Long> roleIds) {
        User user = getById(id);
        Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new BusinessException("角色不存在: " + roleId)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userRepository.save(user);
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
