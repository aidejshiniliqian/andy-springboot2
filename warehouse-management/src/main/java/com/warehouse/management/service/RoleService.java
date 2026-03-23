package com.warehouse.management.service;

import com.warehouse.management.entity.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByCode(String code);
    List<Role> findAll();
    Page<Role> findAll(Page<Role> pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
