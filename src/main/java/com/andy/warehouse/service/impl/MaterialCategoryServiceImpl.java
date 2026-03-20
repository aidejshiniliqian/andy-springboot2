package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.repository.MaterialCategoryRepository;
import com.andy.warehouse.service.MaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MaterialCategory create(MaterialCategoryCreateRequest request) {
        if (request.getCode() != null && categoryRepository.existsByCode(request.getCode())) {
            throw new BusinessException("分类编码已存在");
        }
        MaterialCategory category = new MaterialCategory();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());
        if (request.getParentId() != null) {
            MaterialCategory parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级分类不存在"));
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public MaterialCategory update(MaterialCategoryUpdateRequest request) {
        MaterialCategory category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
        if (category.getDeleted()) {
            throw new BusinessException("分类已被删除");
        }
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(category.getId())) {
                throw new BusinessException("不能将自己设置为父级");
            }
            MaterialCategory parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级分类不存在"));
            category.setParent(parent);
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        List<MaterialCategory> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子分类，无法删除");
        }
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public MaterialCategory getById(Long id) {
        return categoryRepository.findById(id)
                .filter(c -> !c.getDeleted())
                .orElseThrow(() -> new BusinessException("分类不存在"));
    }

    @Override
    public List<MaterialCategory> getAll() {
        return categoryRepository.findAllActive();
    }

    @Override
    public List<MaterialCategory> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    public List<MaterialCategory> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public List<MaterialCategory> getTree() {
        List<MaterialCategory> all = categoryRepository.findAllActive();
        Map<Long, MaterialCategory> map = new HashMap<>();
        List<MaterialCategory> roots = new ArrayList<>();
        for (MaterialCategory cat : all) {
            map.put(cat.getId(), cat);
        }
        for (MaterialCategory cat : all) {
            if (cat.getParent() == null) {
                roots.add(cat);
            } else {
                MaterialCategory parent = map.get(cat.getParent().getId());
            }
        }
        return roots;
    }
}
