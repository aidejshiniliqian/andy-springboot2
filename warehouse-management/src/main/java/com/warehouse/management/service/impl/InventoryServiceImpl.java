package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Inventory;
import com.warehouse.management.repository.InventoryRepository;
import com.warehouse.management.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Optional<Inventory> findByWarehouseIdAndMaterialId(Long warehouseId, Long materialId) {
        return inventoryRepository.findByWarehouseIdAndMaterialId(warehouseId, materialId);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Page<Inventory> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }
}
