package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.warehouse.*;
import com.andy.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse:create')")
    public Result<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        return Result.success("创建成功", warehouseService.createWarehouse(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public Result<WarehouseDTO> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseUpdateRequest request) {
        return Result.success("更新成功", warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:delete')")
    public Result<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public Result<WarehouseDTO> getWarehouseById(@PathVariable Long id) {
        return Result.success(warehouseService.getWarehouseById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('warehouse:read')")
    public Result<PageResult<WarehouseDTO>> getWarehouseList(WarehouseQueryRequest request) {
        return Result.success(warehouseService.getWarehouseList(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public Result<List<WarehouseDTO>> getAllWarehouses() {
        return Result.success(warehouseService.getAllWarehouses());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public Result<Void> updateWarehouseStatus(@PathVariable Long id, @RequestParam Integer status) {
        warehouseService.updateWarehouseStatus(id, status);
        return Result.success("状态更新成功");
    }
}
