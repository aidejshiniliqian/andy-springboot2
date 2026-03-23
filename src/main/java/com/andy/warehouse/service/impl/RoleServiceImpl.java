package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.RoleCreateRequest;
import com.andy.warehouse.dto.RoleUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.RolePermission;
import com.andy.warehouse.mapper.PermissionMapper;
import com.andy.warehouse.mapper.RoleMapper;
import com.andy.warehouse.mapper.RolePermissionMapper;
import com.andy.warehouse.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional
    public Role create(RoleCreateRequest request) {
        if (request.getCode() != null && roleMapper.existsByCode(request.getCode())) {
            throw new BusinessException("角色编码已存在");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());
        roleMapper.insert(role);
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionMapper.selectById(permissionId);
                if (permission == null) {
                    throw new BusinessException("权限不存在: " + permissionId);
                }
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(role.getId());
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        return role;
    }

    @Override
    @Transactional
    public Role update(RoleUpdateRequest request) {
        Role role = roleMapper.selectById(request.getId());
        if (role == null || role.getDeleted()) {
            throw new BusinessException("角色不存在");
        }
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        roleMapper.updateById(role);
        if (request.getPermissionIds() != null) {
            rolePermissionMapper.deleteByRoleId(role.getId());
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionMapper.selectById(permissionId);
                if (permission == null) {
                    throw new BusinessException("权限不存在: " + permissionId);
                }
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(role.getId());
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        return role;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setDeleted(true);
        roleMapper.updateById(role);
    }

    @Override
    public Role getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getDeleted()) {
            throw new BusinessException("角色不存在");
        }
        loadRolePermissions(role);
        return role;
    }

    private void loadRolePermissions(Role role) {
        List<Permission> permissions = rolePermissionMapper.findPermissionsByRoleId(role.getId());
        role.setPermissions(new HashSet<>(permissions));
    }

    @Override
    public List<Role> getAll() {
        return roleMapper.findAllActive();
    }

    @Override
    public Page<Role> getPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDeleted, false);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Role::getName, keyword).or().like(Role::getCode, keyword));
        }
        wrapper.orderByDesc(Role::getCreatedAt);
        IPage<Role> rolePage = roleMapper.selectPage(page, wrapper);
        for (Role role : rolePage.getRecords()) {
            loadRolePermissions(role);
        }
        return (Page<Role>) rolePage;
    }

    @Override
    @Transactional
    public void assignPermissions(Long id, Set<Long> permissionIds) {
        Role role = getById(id);
        rolePermissionMapper.deleteByRoleId(role.getId());
        for (Long permissionId : permissionIds) {
            Permission permission = permissionMapper.selectById(permissionId);
            if (permission == null) {
                throw new BusinessException("权限不存在: " + permissionId);
            }
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    public Set<Permission> getRolePermissions(Long id) {
        Role role = getById(id);
        return role.getPermissions();
    }
}
