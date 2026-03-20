package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Warehouse;
import com.warehouse.management.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public Result<Warehouse> create(@RequestBody Warehouse warehouse) {
        if (warehouseService.existsByCode(warehouse.getCode())) {
            return Result.error("仓库编码已存在");
        }
        return Result.success(warehouseService.save(warehouse));
    }

    @PutMapping("/{id}")
    public Result<Warehouse> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        return warehouseService.findById(id)
                .map(existing -> {
                    warehouse.setId(id);
                    return Result.success(warehouseService.save(warehouse));
                })
                .orElse(Result.error("仓库不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (warehouseService.findById(id).isEmpty()) {
            return Result.error("仓库不存在");
        }
        warehouseService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Warehouse> findById(@PathVariable Long id) {
        return warehouseService.findById(id)
                .map(Result::success)
                .orElse(Result.error("仓库不存在"));
    }

    @GetMapping
    public Result<List<Warehouse>> findAll() {
        return Result.success(warehouseService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<Warehouse>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(warehouseService.findAll(pageable));
    }
}
