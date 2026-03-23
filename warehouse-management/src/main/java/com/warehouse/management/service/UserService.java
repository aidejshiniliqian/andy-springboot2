package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    Page<User> findAll(Page<User> pageable);
    void deleteById(Long id);
    boolean existsByUsername(String username);
}
