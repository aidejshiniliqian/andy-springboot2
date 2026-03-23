package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.warehouse.*;

import java.util.List;

public interface WarehouseService {

    WarehouseDTO createWarehouse(WarehouseCreateRequest request);

    WarehouseDTO updateWarehouse(Long id, WarehouseUpdateRequest request);

    void deleteWarehouse(Long id);

    WarehouseDTO getWarehouseById(Long id);

    PageResult<WarehouseDTO> getWarehouseList(WarehouseQueryRequest request);

    List<WarehouseDTO> getAllWarehouses();

    void updateWarehouseStatus(Long id, Integer status);
}
