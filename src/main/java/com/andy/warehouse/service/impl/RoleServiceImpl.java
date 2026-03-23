package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.role.*;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.PermissionMapper;
import com.andy.warehouse.mapper.RoleMapper;
import com.andy.warehouse.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional
    public RoleDTO createRole(RoleCreateRequest request) {
        if (roleMapper.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        roleMapper.insert(role);

        // 保存角色权限关联
        if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
            for (Long permissionId : request.getPermissionIds()) {
                roleMapper.insertRolePermission(role.getId(), permissionId);
            }
        }

        return convertToDTO(role);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleUpdateRequest request) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }

        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }

        roleMapper.updateById(role);

        // 更新角色权限关联
        if (request.getPermissionIds() != null) {
            roleMapper.deleteRolePermissionsByRoleId(id);
            for (Long permissionId : request.getPermissionIds()) {
                roleMapper.insertRolePermission(id, permissionId);
            }
        }

        return convertToDTO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }
        roleMapper.deleteById(id);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }
        return convertToDTO(role);
    }

    @Override
    public PageResult<RoleDTO> getRoleList(RoleQueryRequest request) {
        Page<Role> page = new Page<>(request.getPage(), request.getSize());
        IPage<Role> rolePage = roleMapper.findByConditions(
                page,
                request.getRoleCode(),
                request.getRoleName(),
                request.getStatus()
        );
        List<RoleDTO> dtoList = rolePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, rolePage.getTotal(), rolePage.getCurrent(), rolePage.getSize());
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDeleted, false);
        return roleMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRoleStatus(Long id, Integer status) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }
        
        roleMapper.deleteRolePermissionsByRoleId(roleId);
        for (Long permissionId : permissionIds) {
            roleMapper.insertRolePermission(roleId, permissionId);
        }
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        
        List<Long> permissionIds = roleMapper.findPermissionIdsByRoleId(role.getId());
        if (!CollectionUtils.isEmpty(permissionIds)) {
            dto.setPermissionIds(permissionIds);
            List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
            dto.setPermissionNames(permissions.stream().map(Permission::getPermissionName).collect(Collectors.toList()));
        }
        
        return dto;
    }
}
