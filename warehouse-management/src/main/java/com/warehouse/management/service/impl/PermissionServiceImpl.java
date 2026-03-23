package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Permission;
import com.warehouse.management.mapper.PermissionMapper;
import com.warehouse.management.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public Permission save(Permission permission) {
        saveOrUpdate(permission);
        return permission;
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<Permission> findAll() {
        return list();
    }

    @Override
    public List<Permission> findRootPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Permission::getParentId);
        return list(wrapper);
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, parentId);
        return list(wrapper);
    }

    @Override
    public Page<Permission> findAll(Page<Permission> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }
}
