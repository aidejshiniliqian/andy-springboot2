package com.warehouse.management.service;

import com.warehouse.management.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    Permission save(Permission permission);
    Optional<Permission> findById(Long id);
    List<Permission> findAll();
    List<Permission> findRootPermissions();
    List<Permission> findByParentId(Long parentId);
    Page<Permission> findAll(Pageable pageable);
    void deleteById(Long id);
}
