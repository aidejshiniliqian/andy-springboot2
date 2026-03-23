package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.material.MaterialCategoryDTO;
import com.andy.warehouse.dto.material.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.material.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.MaterialCategoryMapper;
import com.andy.warehouse.service.MaterialCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryMapper categoryMapper;

    @Override
    @Transactional
    public MaterialCategoryDTO createCategory(MaterialCategoryCreateRequest request) {
        if (categoryMapper.existsByCategoryCode(request.getCategoryCode())) {
            throw new BusinessException("分类编码已存在");
        }

        MaterialCategory category = new MaterialCategory();
        BeanUtils.copyProperties(request, category);
        category.setStatus(1);

        categoryMapper.insert(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public MaterialCategoryDTO updateCategory(Long id, MaterialCategoryUpdateRequest request) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("物资分类不存在");
        }

        if (StringUtils.hasText(request.getCategoryName())) {
            category.setCategoryName(request.getCategoryName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            category.setDescription(request.getDescription());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }

        categoryMapper.updateById(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("物资分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public MaterialCategoryDTO getCategoryById(Long id) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("物资分类不存在");
        }
        return convertToDTO(category);
    }

    @Override
    public List<MaterialCategoryDTO> getCategoryTree() {
        List<MaterialCategory> rootCategories = categoryMapper.findByParentIsNullAndStatusOrderBySortOrderAsc(1);
        return rootCategories.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialCategoryDTO> getAllCategories() {
        return categoryMapper.findByStatus(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long id, Integer status) {
        MaterialCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("物资分类不存在");
        }
        category.setStatus(status);
        categoryMapper.updateById(category);
    }

    private MaterialCategoryDTO convertToDTO(MaterialCategory category) {
        MaterialCategoryDTO dto = new MaterialCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        if (category.getParentId() != null) {
            dto.setParentId(category.getParentId());
            MaterialCategory parent = categoryMapper.selectById(category.getParentId());
            if (parent != null) {
                dto.setParentName(parent.getCategoryName());
            }
        }
        return dto;
    }

    private MaterialCategoryDTO convertToTreeDTO(MaterialCategory category) {
        MaterialCategoryDTO dto = convertToDTO(category);
        List<MaterialCategory> children = categoryMapper.findByParentIdAndStatusOrderBySortOrderAsc(category.getId(), 1);
        if (!CollectionUtils.isEmpty(children)) {
            List<MaterialCategoryDTO> childrenDTO = children.stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTO);
        }
        return dto;
    }
}
