package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.entity.Stock;
import com.andy.warehouse.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存管理")
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "根据仓库和物资查询库存")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/query")
    public Result<Stock> getByWarehouseAndMaterial(
            @RequestParam Long warehouseId,
            @RequestParam Long materialId) {
        return Result.success(stockService.getByWarehouseAndMaterial(warehouseId, materialId));
    }

    @Operation(summary = "根据仓库查询库存")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<Stock>> getByWarehouseId(@PathVariable Long warehouseId) {
        return Result.success(stockService.getByWarehouseId(warehouseId));
    }

    @Operation(summary = "根据物资查询库存")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/material/{materialId}")
    public Result<List<Stock>> getByMaterialId(@PathVariable Long materialId) {
        return Result.success(stockService.getByMaterialId(materialId));
    }

    @Operation(summary = "获取物资总库存")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/total/{materialId}")
    public Result<Integer> getTotalQuantityByMaterialId(@PathVariable Long materialId) {
        return Result.success(stockService.getTotalQuantityByMaterialId(materialId));
    }
}
