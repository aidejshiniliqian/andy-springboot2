package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public Result<Inventory> findById(@PathVariable Long id) {
        return inventoryService.findById(id)
                .map(Result::success)
                .orElse(Result.error("库存不存在"));
    }

    @GetMapping("/warehouse/{warehouseId}/material/{materialId}")
    public Result<Inventory> findByWarehouseAndMaterial(
            @PathVariable Long warehouseId,
            @PathVariable Long materialId) {
        Optional<Inventory> inventory = inventoryService.findByWarehouseIdAndMaterialId(warehouseId, materialId);
        return inventory.map(Result::success).orElseGet(() -> Result.success(null));
    }

    @GetMapping
    public Result<List<Inventory>> findAll() {
        return Result.success(inventoryService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<Inventory>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(inventoryService.findAll(pageable));
    }
}
