package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.material.*;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.MaterialCategoryMapper;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.service.MaterialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final MaterialCategoryMapper categoryMapper;

    @Override
    @Transactional
    public MaterialDTO createMaterial(MaterialCreateRequest request) {
        if (materialMapper.existsByMaterialCode(request.getMaterialCode())) {
            throw new BusinessException("物资编码已存在");
        }
        if (StringUtils.hasText(request.getBarcode()) && materialMapper.existsByBarcode(request.getBarcode())) {
            throw new BusinessException("条码已存在");
        }

        Material material = new Material();
        BeanUtils.copyProperties(request, material);
        material.setStatus(1);

        materialMapper.insert(material);
        return convertToDTO(material);
    }

    @Override
    @Transactional
    public MaterialDTO updateMaterial(Long id, MaterialUpdateRequest request) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new ResourceNotFoundException("物资不存在");
        }

        if (StringUtils.hasText(request.getMaterialName())) {
            material.setMaterialName(request.getMaterialName());
        }
        if (StringUtils.hasText(request.getSpecification())) {
            material.setSpecification(request.getSpecification());
        }
        if (StringUtils.hasText(request.getModel())) {
            material.setModel(request.getModel());
        }
        if (StringUtils.hasText(request.getUnit())) {
            material.setUnit(request.getUnit());
        }
        if (request.getPurchasePrice() != null) {
            material.setPurchasePrice(request.getPurchasePrice());
        }
        if (request.getSalePrice() != null) {
            material.setSalePrice(request.getSalePrice());
        }
        if (request.getSafetyStock() != null) {
            material.setSafetyStock(request.getSafetyStock());
        }
        if (request.getMaxStock() != null) {
            material.setMaxStock(request.getMaxStock());
        }
        if (StringUtils.hasText(request.getDescription())) {
            material.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            material.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            material.setCategoryId(request.getCategoryId());
        }

        materialMapper.updateById(material);
        return convertToDTO(material);
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new ResourceNotFoundException("物资不存在");
        }
        materialMapper.deleteById(id);
    }

    @Override
    public MaterialDTO getMaterialById(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new ResourceNotFoundException("物资不存在");
        }
        return convertToDTO(material);
    }

    @Override
    public MaterialDTO getMaterialByCode(String materialCode) {
        Material material = materialMapper.findByMaterialCode(materialCode)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));
        return convertToDTO(material);
    }

    @Override
    public PageResult<MaterialDTO> getMaterialList(MaterialQueryRequest request) {
        Page<Material> page = new Page<>(request.getPage(), request.getSize());
        IPage<Material> materialPage = materialMapper.findByConditions(
                page,
                request.getMaterialCode(),
                request.getMaterialName(),
                request.getCategoryId(),
                request.getStatus()
        );
        List<MaterialDTO> dtoList = materialPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, materialPage.getTotal(), materialPage.getCurrent(), materialPage.getSize());
    }

    @Override
    public List<MaterialDTO> getAllMaterials() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getIsDeleted, false);
        return materialMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialDTO> getMaterialsByCategoryId(Long categoryId) {
        return materialMapper.findByCategoryIdAndStatus(categoryId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMaterialStatus(Long id, Integer status) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new ResourceNotFoundException("物资不存在");
        }
        material.setStatus(status);
        materialMapper.updateById(material);
    }

    private MaterialDTO convertToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        BeanUtils.copyProperties(material, dto);
        if (material.getCategoryId() != null) {
            dto.setCategoryId(material.getCategoryId());
            MaterialCategory category = categoryMapper.selectById(material.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getCategoryName());
            }
        }
        return dto;
    }
}
