package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    Permission save(Permission permission);
    Optional<Permission> findById(Long id);
    List<Permission> findAll();
    List<Permission> findRootPermissions();
    List<Permission> findByParentId(Long parentId);
    Page<Permission> findAll(Page<Permission> pageable);
    void deleteById(Long id);
}
