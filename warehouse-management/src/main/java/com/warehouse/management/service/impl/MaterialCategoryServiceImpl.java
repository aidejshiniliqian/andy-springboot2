package com.warehouse.management.service.impl;

import com.warehouse.management.entity.MaterialCategory;
import com.warehouse.management.repository.MaterialCategoryRepository;
import com.warehouse.management.service.MaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository categoryRepository;

    @Override
    public MaterialCategory save(MaterialCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<MaterialCategory> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<MaterialCategory> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<MaterialCategory> findRootCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    @Override
    public List<MaterialCategory> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public Page<MaterialCategory> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }
}
