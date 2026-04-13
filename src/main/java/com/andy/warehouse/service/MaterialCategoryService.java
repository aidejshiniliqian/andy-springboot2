package com.andy.warehouse.service;

import com.andy.warehouse.dto.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;

import java.util.List;

public interface MaterialCategoryService {

    MaterialCategory create(MaterialCategoryCreateRequest request);

    MaterialCategory update(MaterialCategoryUpdateRequest request);

    void delete(Long id);

    MaterialCategory getById(Long id);

    List<MaterialCategory> getAll();

    List<MaterialCategory> getRootCategories();

    List<MaterialCategory> getChildren(Long parentId);

    List<MaterialCategory> getTree();
}
