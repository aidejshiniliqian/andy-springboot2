package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.material.MaterialCategoryDTO;
import com.andy.warehouse.dto.material.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.material.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.MaterialCategoryRepository;
import com.andy.warehouse.service.MaterialCategoryService;
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

    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MaterialCategoryDTO createCategory(MaterialCategoryCreateRequest request) {
        if (categoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw new BusinessException("分类编码已存在");
        }

        MaterialCategory category = new MaterialCategory();
        BeanUtils.copyProperties(request, category);
        category.setStatus(1);

        if (request.getParentId() != null) {
            MaterialCategory parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父分类不存在"));
            category.setParent(parent);
        }

        MaterialCategory savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional
    public MaterialCategoryDTO updateCategory(Long id, MaterialCategoryUpdateRequest request) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));

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
            MaterialCategory parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父分类不存在"));
            category.setParent(parent);
        }

        MaterialCategory updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public MaterialCategoryDTO getCategoryById(Long id) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));
        return convertToDTO(category);
    }

    @Override
    public List<MaterialCategoryDTO> getCategoryTree() {
        List<MaterialCategory> rootCategories = categoryRepository.findByParentIsNullAndStatusOrderBySortOrderAsc(1);
        return rootCategories.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialCategoryDTO> getAllCategories() {
        return categoryRepository.findByStatus(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long id, Integer status) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));
        category.setStatus(status);
        categoryRepository.save(category);
    }

    private MaterialCategoryDTO convertToDTO(MaterialCategory category) {
        MaterialCategoryDTO dto = new MaterialCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getCategoryName());
        }
        return dto;
    }

    private MaterialCategoryDTO convertToTreeDTO(MaterialCategory category) {
        MaterialCategoryDTO dto = convertToDTO(category);
        if (!CollectionUtils.isEmpty(category.getChildren())) {
            List<MaterialCategoryDTO> children = category.getChildren().stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }
}
