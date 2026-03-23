package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.MaterialCategory;
import com.warehouse.management.mapper.MaterialCategoryMapper;
import com.warehouse.management.service.MaterialCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialCategoryServiceImpl extends ServiceImpl<MaterialCategoryMapper, MaterialCategory> implements MaterialCategoryService {

    @Override
    public MaterialCategory save(MaterialCategory category) {
        saveOrUpdate(category);
        return category;
    }

    @Override
    public Optional<MaterialCategory> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<MaterialCategory> findAll() {
        return list();
    }

    @Override
    public List<MaterialCategory> findRootCategories() {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(MaterialCategory::getParentId);
        return list(wrapper);
    }

    @Override
    public List<MaterialCategory> findByParentId(Long parentId) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, parentId);
        return list(wrapper);
    }

    @Override
    public Page<MaterialCategory> findAll(Page<MaterialCategory> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCode, code);
        return count(wrapper) > 0;
    }
}
