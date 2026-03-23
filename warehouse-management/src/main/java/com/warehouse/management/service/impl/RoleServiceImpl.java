package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Role;
import com.warehouse.management.mapper.RoleMapper;
import com.warehouse.management.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Role save(Role role) {
        this.saveOrUpdate(role);
        return role;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(this.getById(id));
    }

    @Override
    public List<Role> findAll() {
        return this.list();
    }

    @Override
    public Page<Role> findAll(Page<Role> pageable) {
        return this.page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, code);
        return this.count(wrapper) > 0;
    }
}
