package com.andy.warehouse.service;

import com.andy.warehouse.dto.PermissionCreateRequest;
import com.andy.warehouse.dto.PermissionUpdateRequest;
import com.andy.warehouse.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission create(PermissionCreateRequest request);

    Permission update(PermissionUpdateRequest request);

    void delete(Long id);

    Permission getById(Long id);

    List<Permission> getAll();

    List<Permission> getRootPermissions();

    List<Permission> getChildren(Long parentId);

    List<Permission> getTree();
}
