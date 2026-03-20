package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.user.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.*;
import com.andy.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        if (request.getOrgId() != null) {
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));
            user.setOrganization(org);
        }

        if (request.getDeptId() != null) {
            Department dept = departmentRepository.findById(request.getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
            user.setDepartment(dept);
        }

        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        if (request.getOrgId() != null) {
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));
            user.setOrganization(org);
        }

        if (request.getDeptId() != null) {
            Department dept = departmentRepository.findById(request.getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
            user.setDepartment(dept);
        }

        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    public PageResult<UserDTO> getUserList(UserQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findByConditions(
                request.getUsername(),
                request.getRealName(),
                request.getOrgId(),
                request.getDeptId(),
                request.getStatus(),
                pageable
        );
        return PageResult.of(userPage.map(this::convertToDTO));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        
        if (user.getOrganization() != null) {
            dto.setOrgId(user.getOrganization().getId());
            dto.setOrgName(user.getOrganization().getOrgName());
        }
        
        if (user.getDepartment() != null) {
            dto.setDeptId(user.getDepartment().getId());
            dto.setDeptName(user.getDepartment().getDeptName());
        }
        
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            dto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
            dto.setRoleNames(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
        }
        
        return dto;
    }
}
