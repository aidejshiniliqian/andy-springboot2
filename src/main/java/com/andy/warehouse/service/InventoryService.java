package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.InventoryDTO;
import com.andy.warehouse.dto.stock.InventoryQueryRequest;
import com.andy.warehouse.dto.stock.InventoryRecordDTO;
import com.andy.warehouse.dto.stock.InventoryRecordQueryRequest;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    InventoryDTO getInventoryById(Long id);

    PageResult<InventoryDTO> getInventoryList(InventoryQueryRequest request);

    List<InventoryDTO> getInventoryByMaterialId(Long materialId);

    List<InventoryDTO> getInventoryByWarehouseId(Long warehouseId);

    BigDecimal getTotalQuantityByMaterialId(Long materialId);

    PageResult<InventoryRecordDTO> getInventoryRecordList(InventoryRecordQueryRequest request);

    List<InventoryRecordDTO> getInventoryRecordsByMaterialAndWarehouse(Long materialId, Long warehouseId, int limit);
}
