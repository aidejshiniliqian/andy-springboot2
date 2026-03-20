package com.warehouse.management.service;

import com.warehouse.management.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialService {
    Material save(Material material);
    Optional<Material> findById(Long id);
    List<Material> findAll();
    Page<Material> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
