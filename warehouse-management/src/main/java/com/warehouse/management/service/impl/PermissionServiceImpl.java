package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Permission;
import com.warehouse.management.repository.PermissionRepository;
import com.warehouse.management.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> findRootPermissions() {
        return permissionRepository.findByParentIdIsNull();
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    @Override
    public Page<Permission> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}
