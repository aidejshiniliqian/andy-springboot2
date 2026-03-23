package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.Material;

import java.util.List;
import java.util.Optional;

public interface MaterialService {
    Material save(Material material);
    Optional<Material> findById(Long id);
    List<Material> findAll();
    Page<Material> findAll(Page<Material> pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
