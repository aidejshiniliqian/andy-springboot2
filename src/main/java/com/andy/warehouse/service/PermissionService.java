package com.andy.warehouse.service;

import com.andy.warehouse.dto.permission.PermissionCreateRequest;
import com.andy.warehouse.dto.permission.PermissionDTO;
import com.andy.warehouse.dto.permission.PermissionUpdateRequest;

import java.util.List;

public interface PermissionService {

    PermissionDTO createPermission(PermissionCreateRequest request);

    PermissionDTO updatePermission(Long id, PermissionUpdateRequest request);

    void deletePermission(Long id);

    PermissionDTO getPermissionById(Long id);

    List<PermissionDTO> getAllPermissions();

    List<PermissionDTO> getPermissionTree();

    List<PermissionDTO> getPermissionsByRoleId(Long roleId);

    List<PermissionDTO> getPermissionsByUserId(Long userId);

    void updatePermissionStatus(Long id, Integer status);
}
