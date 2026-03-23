package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.permission.PermissionCreateRequest;
import com.andy.warehouse.dto.permission.PermissionDTO;
import com.andy.warehouse.dto.permission.PermissionUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.PermissionMapper;
import com.andy.warehouse.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    @Transactional
    public PermissionDTO createPermission(PermissionCreateRequest request) {
        if (permissionMapper.existsByPermissionCode(request.getPermissionCode())) {
            throw new BusinessException("权限编码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);
        permission.setStatus(1);

        permissionMapper.insert(permission);
        return convertToDTO(permission);
    }

    @Override
    @Transactional
    public PermissionDTO updatePermission(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }

        if (StringUtils.hasText(request.getPermissionName())) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            permission.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getType())) {
            permission.setType(request.getType());
        }
        if (StringUtils.hasText(request.getResourceUrl())) {
            permission.setResourceUrl(request.getResourceUrl());
        }
        if (StringUtils.hasText(request.getHttpMethod())) {
            permission.setHttpMethod(request.getHttpMethod());
        }
        if (request.getSortOrder() != null) {
            permission.setSortOrder(request.getSortOrder());
        }
        if (StringUtils.hasText(request.getIcon())) {
            permission.setIcon(request.getIcon());
        }
        if (request.getStatus() != null) {
            permission.setStatus(request.getStatus());
        }

        permissionMapper.updateById(permission);
        return convertToDTO(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }
        permissionMapper.deleteById(id);
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }
        return convertToDTO(permission);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getIsDeleted, false);
        return permissionMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        List<Permission> rootPermissions = permissionMapper.findByParentIsNullAndStatusOrderBySortOrderAsc(1);
        return rootPermissions.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByUserId(Long userId) {
        return permissionMapper.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePermissionStatus(Long id, Integer status) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }
        permission.setStatus(status);
        permissionMapper.updateById(permission);
    }

    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(permission, dto);
        if (permission.getParentId() != null) {
            dto.setParentId(permission.getParentId());
            Permission parent = permissionMapper.selectById(permission.getParentId());
            if (parent != null) {
                dto.setParentName(parent.getPermissionName());
            }
        }
        return dto;
    }

    private PermissionDTO convertToTreeDTO(Permission permission) {
        PermissionDTO dto = convertToDTO(permission);
        List<Permission> children = permissionMapper.findByParentIdAndStatusOrderBySortOrderAsc(permission.getId(), 1);
        if (!CollectionUtils.isEmpty(children)) {
            List<PermissionDTO> childrenDTO = children.stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTO);
        }
        return dto;
    }
}
