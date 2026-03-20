package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.role.*;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.repository.RoleRepository;
import com.andy.warehouse.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RoleDTO createRole(RoleCreateRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));

        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }

        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        role.setIsDeleted(true);
        roleRepository.save(role);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        return convertToDTO(role);
    }

    @Override
    public PageResult<RoleDTO> getRoleList(RoleQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Role> rolePage = roleRepository.findByConditions(
                request.getRoleCode(),
                request.getRoleName(),
                request.getStatus(),
                pageable
        );
        return PageResult.of(rolePage.map(this::convertToDTO));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .filter(role -> !Boolean.TRUE.equals(role.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRoleStatus(Long id, Integer status) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        role.setStatus(status);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        
        if (!CollectionUtils.isEmpty(role.getPermissions())) {
            dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
            dto.setPermissionNames(role.getPermissions().stream().map(Permission::getPermissionName).collect(Collectors.toList()));
        }
        
        return dto;
    }
}
