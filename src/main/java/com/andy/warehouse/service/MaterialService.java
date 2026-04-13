package com.andy.warehouse.service;

import com.andy.warehouse.dto.MaterialCreateRequest;
import com.andy.warehouse.dto.MaterialUpdateRequest;
import com.andy.warehouse.entity.Material;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface MaterialService {

    Material create(MaterialCreateRequest request);

    Material update(MaterialUpdateRequest request);

    void delete(Long id);

    Material getById(Long id);

    Material getByCode(String code);

    Material getByBarcode(String barcode);

    List<Material> getAll();

    List<Material> getByCategoryId(Long categoryId);

    Page<Material> getPage(Long categoryId, Integer pageNum, Integer pageSize, String keyword);
}
