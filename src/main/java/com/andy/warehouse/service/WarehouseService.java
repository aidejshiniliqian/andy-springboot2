package com.andy.warehouse.service;

import com.andy.warehouse.dto.WarehouseCreateRequest;
import com.andy.warehouse.dto.WarehouseUpdateRequest;
import com.andy.warehouse.entity.Warehouse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface WarehouseService {

    Warehouse create(WarehouseCreateRequest request);

    Warehouse update(WarehouseUpdateRequest request);

    void delete(Long id);

    Warehouse getById(Long id);

    List<Warehouse> getAll();

    Page<Warehouse> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword);
}
