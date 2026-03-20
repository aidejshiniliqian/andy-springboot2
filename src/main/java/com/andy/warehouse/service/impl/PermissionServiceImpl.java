package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.PermissionCreateRequest;
import com.andy.warehouse.dto.PermissionUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public Permission create(PermissionCreateRequest request) {
        if (request.getCode() != null && permissionRepository.existsByCode(request.getCode())) {
            throw new BusinessException("权限编码已存在");
        }
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setType(request.getType());
        permission.setParentId(request.getParentId());
        permission.setPath(request.getPath());
        permission.setComponent(request.getComponent());
        permission.setIcon(request.getIcon());
        permission.setSortOrder(request.getSortOrder());
        permission.setStatus(request.getStatus());
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission update(PermissionUpdateRequest request) {
        Permission permission = permissionRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("权限不存在"));
        if (permission.getDeleted()) {
            throw new BusinessException("权限已被删除");
        }
        if (request.getName() != null) {
            permission.setName(request.getName());
        }
        if (request.getPath() != null) {
            permission.setPath(request.getPath());
        }
        if (request.getComponent() != null) {
            permission.setComponent(request.getComponent());
        }
        if (request.getIcon() != null) {
            permission.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            permission.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            permission.setStatus(request.getStatus());
        }
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        List<Permission> children = permissionRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子权限，无法删除");
        }
        permission.setDeleted(true);
        permissionRepository.save(permission);
    }

    @Override
    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new BusinessException("权限不存在"));
    }

    @Override
    public List<Permission> getAll() {
        return permissionRepository.findAllActive();
    }

    @Override
    public List<Permission> getRootPermissions() {
        return permissionRepository.findRootPermissions();
    }

    @Override
    public List<Permission> getChildren(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    @Override
    public List<Permission> getTree() {
        List<Permission> all = permissionRepository.findAllActive();
        Map<Long, Permission> map = new HashMap<>();
        List<Permission> roots = new ArrayList<>();
        for (Permission perm : all) {
            map.put(perm.getId(), perm);
        }
        for (Permission perm : all) {
            if (perm.getParentId() == null) {
                roots.add(perm);
            } else {
                Permission parent = map.get(perm.getParentId());
            }
        }
        return roots;
    }
}
