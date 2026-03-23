package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.WarehouseCreateRequest;
import com.andy.warehouse.dto.WarehouseUpdateRequest;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.service.WarehouseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仓库管理")
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "创建仓库")
    @PreAuthorize("hasAuthority('warehouse:create')")
    @PostMapping
    public Result<Warehouse> create(@Valid @RequestBody WarehouseCreateRequest request) {
        return Result.success(warehouseService.create(request));
    }

    @Operation(summary = "更新仓库")
    @PreAuthorize("hasAuthority('warehouse:update')")
    @PutMapping
    public Result<Warehouse> update(@Valid @RequestBody WarehouseUpdateRequest request) {
        return Result.success(warehouseService.update(request));
    }

    @Operation(summary = "删除仓库")
    @PreAuthorize("hasAuthority('warehouse:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取仓库详情")
    @PreAuthorize("hasAuthority('warehouse:view')")
    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable Long id) {
        return Result.success(warehouseService.getById(id));
    }

    @Operation(summary = "获取所有仓库")
    @PreAuthorize("hasAuthority('warehouse:view')")
    @GetMapping
    public Result<List<Warehouse>> getAll() {
        return Result.success(warehouseService.getAll());
    }

    @Operation(summary = "分页查询仓库")
    @PreAuthorize("hasAuthority('warehouse:view')")
    @GetMapping("/page")
    public Result<PageResult<Warehouse>> getPage(
            @RequestParam(required = false) Long orgId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Warehouse> page = warehouseService.getPage(orgId, pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }
}
