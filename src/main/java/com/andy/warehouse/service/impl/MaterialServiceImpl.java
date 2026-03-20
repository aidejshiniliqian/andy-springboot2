package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.MaterialCreateRequest;
import com.andy.warehouse.dto.MaterialUpdateRequest;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.repository.MaterialCategoryRepository;
import com.andy.warehouse.repository.MaterialRepository;
import com.andy.warehouse.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public Material create(MaterialCreateRequest request) {
        if (request.getCode() != null && materialRepository.existsByCode(request.getCode())) {
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
        if (request.getCategoryId() != null) {
            MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException("物资分类不存在"));
            material.setCategory(category);
        }
        return materialRepository.save(material);
    }

    @Override
    @Transactional
    public Material update(MaterialUpdateRequest request) {
        Material material = materialRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("物资不存在"));
        if (material.getDeleted()) {
            throw new BusinessException("物资已被删除");
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
            MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException("物资分类不存在"));
            material.setCategory(category);
        }
        if (request.getStatus() != null) {
            material.setStatus(request.getStatus());
        }
        return materialRepository.save(material);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new BusinessException("物资不存在"));
        material.setDeleted(true);
        materialRepository.save(material);
    }

    @Override
    public Material getById(Long id) {
        return materialRepository.findById(id)
                .filter(m -> !m.getDeleted())
                .orElseThrow(() -> new BusinessException("物资不存在"));
    }

    @Override
    public Material getByCode(String code) {
        return materialRepository.findByCode(code)
                .filter(m -> !m.getDeleted())
                .orElseThrow(() -> new BusinessException("物资不存在"));
    }

    @Override
    public Material getByBarcode(String barcode) {
        return materialRepository.findByBarcode(barcode)
                .filter(m -> !m.getDeleted())
                .orElseThrow(() -> new BusinessException("物资不存在"));
    }

    @Override
    public List<Material> getAll() {
        return materialRepository.findAllActive();
    }

    @Override
    public List<Material> getByCategoryId(Long categoryId) {
        return materialRepository.findByCategoryId(categoryId);
    }

    @Override
    public Page<Material> getPage(Long categoryId, Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<Material> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }
            if (StringUtils.hasText(keyword)) {
                var namePredicate = cb.like(root.get("name"), "%" + keyword + "%");
                var codePredicate = cb.like(root.get("code"), "%" + keyword + "%");
                predicates = cb.and(predicates, cb.or(namePredicate, codePredicate));
            }
            return predicates;
        };
        return materialRepository.findAll(spec, pageable);
    }
}
