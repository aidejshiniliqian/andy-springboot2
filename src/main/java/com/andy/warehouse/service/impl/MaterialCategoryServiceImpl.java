package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.mapper.MaterialCategoryMapper;
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

    private final MaterialCategoryMapper categoryMapper;

    @Override
    @Transactional
    public MaterialCategory create(MaterialCategoryCreateRequest request) {
        if (request.getCode() != null && categoryMapper.existsByCode(request.getCode())) {
            throw new BusinessException("分类编码已存在");
        }
        MaterialCategory category = new MaterialCategory();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());
        category.setParentId(request.getParentId());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public MaterialCategory update(MaterialCategoryUpdateRequest request) {
        MaterialCategory category = categoryMapper.selectById(request.getId());
        if (category == null || category.getDeleted()) {
            throw new BusinessException("分类不存在");
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
            category.setParentId(request.getParentId());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        List<MaterialCategory> children = categoryMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子分类，无法删除");
        }
        category.setDeleted(true);
        categoryMapper.updateById(category);
    }

    @Override
    public MaterialCategory getById(Long id) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted()) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public List<MaterialCategory> getAll() {
        return categoryMapper.findAllActive();
    }

    @Override
    public List<MaterialCategory> getRootCategories() {
        return categoryMapper.findRootCategories();
    }

    @Override
    public List<MaterialCategory> getChildren(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    @Override
    public List<MaterialCategory> getTree() {
        List<MaterialCategory> all = categoryMapper.findAllActive();
        Map<Long, MaterialCategory> map = new HashMap<>();
        List<MaterialCategory> roots = new ArrayList<>();
        for (MaterialCategory cat : all) {
            map.put(cat.getId(), cat);
        }
        for (MaterialCategory cat : all) {
            if (cat.getParentId() == null) {
                roots.add(cat);
            }
        }
        return roots;
    }
}
