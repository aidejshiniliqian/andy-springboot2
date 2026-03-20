package com.warehouse.management.service;

import com.warehouse.management.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByCode(String code);
    List<Role> findAll();
    Page<Role> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
