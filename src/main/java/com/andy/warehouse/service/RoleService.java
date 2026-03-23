package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.role.*;

import java.util.List;

public interface RoleService {

    RoleDTO createRole(RoleCreateRequest request);

    RoleDTO updateRole(Long id, RoleUpdateRequest request);

    void deleteRole(Long id);

    RoleDTO getRoleById(Long id);

    PageResult<RoleDTO> getRoleList(RoleQueryRequest request);

    List<RoleDTO> getAllRoles();

    void updateRoleStatus(Long id, Integer status);

    void assignPermissions(Long roleId, List<Long> permissionIds);
}
