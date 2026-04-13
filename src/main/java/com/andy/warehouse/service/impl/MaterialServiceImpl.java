package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.MaterialCreateRequest;
import com.andy.warehouse.dto.MaterialUpdateRequest;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.mapper.MaterialCategoryMapper;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.service.MaterialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final MaterialCategoryMapper categoryMapper;

    @Override
    @Transactional
    public Material create(MaterialCreateRequest request) {
        if (request.getCode() != null && materialMapper.existsByCode(request.getCode())) {
            throw new BusinessException("物资编码已存在");
        }
        Material material = new Material();
        material.setName(request.getName());
        material.setCode(request.getCode());
        material.setBarcode(request.getBarcode());
        material.setSpecification(request.getSpecification());
        material.setModel(request.getModel());
        material.setUnit(request.getUnit());
        material.setPrice(request.getPrice());
        material.setSafetyStock(request.getSafetyStock());
        material.setMaxStock(request.getMaxStock());
        material.setDescription(request.getDescription());
        material.setStatus(request.getStatus());
        material.setCategoryId(request.getCategoryId());
        materialMapper.insert(material);
        return material;
    }

    @Override
    @Transactional
    public Material update(MaterialUpdateRequest request) {
        Material material = materialMapper.selectById(request.getId());
        if (material == null || material.getDeleted()) {
            throw new BusinessException("物资不存在");
        }
        if (request.getName() != null) {
            material.setName(request.getName());
        }
        if (request.getBarcode() != null) {
            material.setBarcode(request.getBarcode());
        }
        if (request.getSpecification() != null) {
            material.setSpecification(request.getSpecification());
        }
        if (request.getModel() != null) {
            material.setModel(request.getModel());
        }
        if (request.getUnit() != null) {
            material.setUnit(request.getUnit());
        }
        if (request.getPrice() != null) {
            material.setPrice(request.getPrice());
        }
        if (request.getSafetyStock() != null) {
            material.setSafetyStock(request.getSafetyStock());
        }
        if (request.getMaxStock() != null) {
            material.setMaxStock(request.getMaxStock());
        }
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            material.setCategoryId(request.getCategoryId());
        }
        if (request.getStatus() != null) {
            material.setStatus(request.getStatus());
        }
        materialMapper.updateById(material);
        return material;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException("物资不存在");
        }
        material.setDeleted(true);
        materialMapper.updateById(material);
    }

    @Override
    public Material getById(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null || material.getDeleted()) {
            throw new BusinessException("物资不存在");
        }
        loadMaterialCategory(material);
        return material;
    }

    private void loadMaterialCategory(Material material) {
        if (material.getCategoryId() != null) {
            MaterialCategory category = categoryMapper.selectById(material.getCategoryId());
            material.setCategory(category);
        }
    }

    @Override
    public Material getByCode(String code) {
        Material material = materialMapper.findByCode(code);
        if (material == null || material.getDeleted()) {
            throw new BusinessException("物资不存在");
        }
        loadMaterialCategory(material);
        return material;
    }

    @Override
    public Material getByBarcode(String barcode) {
        Material material = materialMapper.findByBarcode(barcode);
        if (material == null || material.getDeleted()) {
            throw new BusinessException("物资不存在");
        }
        loadMaterialCategory(material);
        return material;
    }

    @Override
    public List<Material> getAll() {
        List<Material> materials = materialMapper.findAllActive();
        for (Material material : materials) {
            loadMaterialCategory(material);
        }
        return materials;
    }

    @Override
    public List<Material> getByCategoryId(Long categoryId) {
        List<Material> materials = materialMapper.findByCategoryId(categoryId);
        for (Material material : materials) {
            loadMaterialCategory(material);
        }
        return materials;
    }

    @Override
    public Page<Material> getPage(Long categoryId, Integer pageNum, Integer pageSize, String keyword) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getDeleted, false);
        if (categoryId != null) {
            wrapper.eq(Material::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Material::getName, keyword).or().like(Material::getCode, keyword));
        }
        wrapper.orderByDesc(Material::getCreatedAt);
        IPage<Material> materialPage = materialMapper.selectPage(page, wrapper);
        for (Material material : materialPage.getRecords()) {
            loadMaterialCategory(material);
        }
        return (Page<Material>) materialPage;
    }
}
