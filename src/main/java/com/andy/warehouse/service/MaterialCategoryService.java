package com.andy.warehouse.service;

import com.andy.warehouse.dto.material.MaterialCategoryDTO;
import com.andy.warehouse.dto.material.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.material.MaterialCategoryUpdateRequest;

import java.util.List;

public interface MaterialCategoryService {

    MaterialCategoryDTO createCategory(MaterialCategoryCreateRequest request);

    MaterialCategoryDTO updateCategory(Long id, MaterialCategoryUpdateRequest request);

    void deleteCategory(Long id);

    MaterialCategoryDTO getCategoryById(Long id);

    List<MaterialCategoryDTO> getCategoryTree();

    List<MaterialCategoryDTO> getAllCategories();

    void updateCategoryStatus(Long id, Integer status);
}
