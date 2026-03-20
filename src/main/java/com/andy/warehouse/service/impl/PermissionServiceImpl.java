package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.permission.PermissionCreateRequest;
import com.andy.warehouse.dto.permission.PermissionDTO;
import com.andy.warehouse.dto.permission.PermissionUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.service.PermissionService;
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

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public PermissionDTO createPermission(PermissionCreateRequest request) {
        if (permissionRepository.existsByPermissionCode(request.getPermissionCode())) {
            throw new BusinessException("权限编码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);
        permission.setStatus(1);

        if (request.getParentId() != null) {
            Permission parent = permissionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父权限不存在"));
            permission.setParent(parent);
        }

        Permission savedPermission = permissionRepository.save(permission);
        return convertToDTO(savedPermission);
    }

    @Override
    @Transactional
    public PermissionDTO updatePermission(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在"));

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

        Permission updatedPermission = permissionRepository.save(permission);
        return convertToDTO(updatedPermission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在"));
        permission.setIsDeleted(true);
        permissionRepository.save(permission);
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在"));
        return convertToDTO(permission);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .filter(perm -> !Boolean.TRUE.equals(perm.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        List<Permission> rootPermissions = permissionRepository.findByParentIsNullAndStatusOrderBySortOrderAsc(1);
        return rootPermissions.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByUserId(Long userId) {
        return permissionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePermissionStatus(Long id, Integer status) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在"));
        permission.setStatus(status);
        permissionRepository.save(permission);
    }

    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(permission, dto);
        if (permission.getParent() != null) {
            dto.setParentId(permission.getParent().getId());
            dto.setParentName(permission.getParent().getPermissionName());
        }
        return dto;
    }

    private PermissionDTO convertToTreeDTO(Permission permission) {
        PermissionDTO dto = convertToDTO(permission);
        if (!CollectionUtils.isEmpty(permission.getChildren())) {
            List<PermissionDTO> children = permission.getChildren().stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }
}
