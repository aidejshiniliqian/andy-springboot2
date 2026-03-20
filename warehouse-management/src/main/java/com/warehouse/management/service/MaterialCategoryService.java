package com.warehouse.management.service;

import com.warehouse.management.entity.MaterialCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialCategoryService {
    MaterialCategory save(MaterialCategory category);
    Optional<MaterialCategory> findById(Long id);
    List<MaterialCategory> findAll();
    List<MaterialCategory> findRootCategories();
    List<MaterialCategory> findByParentId(Long parentId);
    Page<MaterialCategory> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
