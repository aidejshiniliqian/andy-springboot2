package com.andy.warehouse.service;

import com.andy.warehouse.dto.RoleCreateRequest;
import com.andy.warehouse.dto.RoleUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Set;

public interface RoleService {

    Role create(RoleCreateRequest request);

    Role update(RoleUpdateRequest request);

    void delete(Long id);

    Role getById(Long id);

    List<Role> getAll();

    Page<Role> getPage(Integer pageNum, Integer pageSize, String keyword);

    void assignPermissions(Long id, Set<Long> permissionIds);

    Set<Permission> getRolePermissions(Long id);
}
