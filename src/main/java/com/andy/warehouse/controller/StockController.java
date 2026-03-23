package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.stock.*;
import com.andy.warehouse.service.InventoryService;
import com.andy.warehouse.service.StockInService;
import com.andy.warehouse.service.StockOutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockInService stockInService;
    private final StockOutService stockOutService;
    private final InventoryService inventoryService;

    @PostMapping("/in")
    @PreAuthorize("hasAuthority('stock:in:create')")
    public Result<StockInOrderDTO> createStockIn(@Valid @RequestBody StockInCreateRequest request) {
        return Result.success("入库单创建成功", stockInService.createStockIn(request));
    }

    @PutMapping("/in/{id}/confirm")
    @PreAuthorize("hasAuthority('stock:in:confirm')")
    public Result<StockInOrderDTO> confirmStockIn(@PathVariable Long id) {
        return Result.success("入库确认成功", stockInService.confirmStockIn(id));
    }

    @PutMapping("/in/{id}/cancel")
    @PreAuthorize("hasAuthority('stock:in:cancel')")
    public Result<StockInOrderDTO> cancelStockIn(@PathVariable Long id) {
        return Result.success("入库取消成功", stockInService.cancelStockIn(id));
    }

    @GetMapping("/in/{id}")
    @PreAuthorize("hasAuthority('stock:in:read')")
    public Result<StockInOrderDTO> getStockInById(@PathVariable Long id) {
        return Result.success(stockInService.getStockInById(id));
    }

    @GetMapping("/in")
    @PreAuthorize("hasAuthority('stock:in:read')")
    public Result<PageResult<StockInOrderDTO>> getStockInList(StockQueryRequest request) {
        return Result.success(stockInService.getStockInList(request));
    }

    @PostMapping("/out")
    @PreAuthorize("hasAuthority('stock:out:create')")
    public Result<StockOutOrderDTO> createStockOut(@Valid @RequestBody StockOutCreateRequest request) {
        return Result.success("出库单创建成功", stockOutService.createStockOut(request));
    }

    @PutMapping("/out/{id}/approve")
    @PreAuthorize("hasAuthority('stock:out:approve')")
    public Result<StockOutOrderDTO> approveStockOut(@PathVariable Long id) {
        return Result.success("出库审批成功", stockOutService.approveStockOut(id));
    }

    @PutMapping("/out/{id}/confirm")
    @PreAuthorize("hasAuthority('stock:out:confirm')")
    public Result<StockOutOrderDTO> confirmStockOut(@PathVariable Long id) {
        return Result.success("出库确认成功", stockOutService.confirmStockOut(id));
    }

    @PutMapping("/out/{id}/cancel")
    @PreAuthorize("hasAuthority('stock:out:cancel')")
    public Result<StockOutOrderDTO> cancelStockOut(@PathVariable Long id) {
        return Result.success("出库取消成功", stockOutService.cancelStockOut(id));
    }

    @GetMapping("/out/{id}")
    @PreAuthorize("hasAuthority('stock:out:read')")
    public Result<StockOutOrderDTO> getStockOutById(@PathVariable Long id) {
        return Result.success(stockOutService.getStockOutById(id));
    }

    @GetMapping("/out")
    @PreAuthorize("hasAuthority('stock:out:read')")
    public Result<PageResult<StockOutOrderDTO>> getStockOutList(StockQueryRequest request) {
        return Result.success(stockOutService.getStockOutList(request));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<PageResult<InventoryDTO>> getInventoryList(InventoryQueryRequest request) {
        return Result.success(inventoryService.getInventoryList(request));
    }

    @GetMapping("/inventory/{id}")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<InventoryDTO> getInventoryById(@PathVariable Long id) {
        return Result.success(inventoryService.getInventoryById(id));
    }

    @GetMapping("/inventory/by-material/{materialId}")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<List<InventoryDTO>> getInventoryByMaterialId(@PathVariable Long materialId) {
        return Result.success(inventoryService.getInventoryByMaterialId(materialId));
    }

    @GetMapping("/inventory/by-warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<List<InventoryDTO>> getInventoryByWarehouseId(@PathVariable Long warehouseId) {
        return Result.success(inventoryService.getInventoryByWarehouseId(warehouseId));
    }

    @GetMapping("/inventory/total/{materialId}")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<java.math.BigDecimal> getTotalQuantityByMaterialId(@PathVariable Long materialId) {
        return Result.success(inventoryService.getTotalQuantityByMaterialId(materialId));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAuthority('inventory:read')")
    public Result<PageResult<InventoryRecordDTO>> getInventoryRecordList(InventoryRecordQueryRequest request) {
        return Result.success(inventoryService.getInventoryRecordList(request));
    }
}
