package com.andy.warehouse.service;

import com.andy.warehouse.entity.Stock;

import java.util.List;

public interface StockService {

    Stock getByWarehouseAndMaterial(Long warehouseId, Long materialId);

    List<Stock> getByWarehouseId(Long warehouseId);

    List<Stock> getByMaterialId(Long materialId);

    Integer getTotalQuantityByMaterialId(Long materialId);

    void addStock(Long warehouseId, Long materialId, Integer quantity, String batchNo, String position);

    void subtractStock(Long warehouseId, Long materialId, Integer quantity);

    void lockStock(Long warehouseId, Long materialId, Integer quantity);

    void unlockStock(Long warehouseId, Long materialId, Integer quantity);
}
