package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.material.*;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.MaterialCategoryRepository;
import com.andy.warehouse.repository.MaterialRepository;
import com.andy.warehouse.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MaterialDTO createMaterial(MaterialCreateRequest request) {
        if (materialRepository.existsByMaterialCode(request.getMaterialCode())) {
            throw new BusinessException("物资编码已存在");
        }
        if (StringUtils.hasText(request.getBarcode()) && materialRepository.existsByBarcode(request.getBarcode())) {
            throw new BusinessException("条码已存在");
        }

        Material material = new Material();
        BeanUtils.copyProperties(request, material);
        material.setStatus(1);

        if (request.getCategoryId() != null) {
            MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));
            material.setCategory(category);
        }

        Material savedMaterial = materialRepository.save(material);
        return convertToDTO(savedMaterial);
    }

    @Override
    @Transactional
    public MaterialDTO updateMaterial(Long id, MaterialUpdateRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));

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
            MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("物资分类不存在"));
            material.setCategory(category);
        }

        Material updatedMaterial = materialRepository.save(material);
        return convertToDTO(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));
        material.setIsDeleted(true);
        materialRepository.save(material);
    }

    @Override
    public MaterialDTO getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));
        return convertToDTO(material);
    }

    @Override
    public MaterialDTO getMaterialByCode(String materialCode) {
        Material material = materialRepository.findByMaterialCode(materialCode)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));
        return convertToDTO(material);
    }

    @Override
    public PageResult<MaterialDTO> getMaterialList(MaterialQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Material> materialPage = materialRepository.findByConditions(
                request.getMaterialCode(),
                request.getMaterialName(),
                request.getCategoryId(),
                request.getStatus(),
                pageable
        );
        return PageResult.of(materialPage.map(this::convertToDTO));
    }

    @Override
    public List<MaterialDTO> getAllMaterials() {
        return materialRepository.findAll().stream()
                .filter(material -> !Boolean.TRUE.equals(material.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialDTO> getMaterialsByCategoryId(Long categoryId) {
        return materialRepository.findByCategoryIdAndStatus(categoryId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMaterialStatus(Long id, Integer status) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));
        material.setStatus(status);
        materialRepository.save(material);
    }

    private MaterialDTO convertToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        BeanUtils.copyProperties(material, dto);
        if (material.getCategory() != null) {
            dto.setCategoryId(material.getCategory().getId());
            dto.setCategoryName(material.getCategory().getCategoryName());
        }
        return dto;
    }
}
