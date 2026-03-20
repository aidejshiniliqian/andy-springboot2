package com.warehouse.management.service;

import com.warehouse.management.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InventoryService {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(Long id);
    Optional<Inventory> findByWarehouseIdAndMaterialId(Long warehouseId, Long materialId);
    List<Inventory> findAll();
    Page<Inventory> findAll(Pageable pageable);
    void deleteById(Long id);
}
