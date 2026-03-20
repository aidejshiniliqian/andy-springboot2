package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.StockOutOrderCreateRequest;
import com.andy.warehouse.entity.StockOutOrder;
import com.andy.warehouse.service.StockOutOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "出库管理")
@RestController
@RequestMapping("/stock-out-orders")
@RequiredArgsConstructor
public class StockOutOrderController {

    private final StockOutOrderService stockOutOrderService;

    @Operation(summary = "创建出库单")
    @PreAuthorize("hasAuthority('stock:create')")
    @PostMapping
    public Result<StockOutOrder> create(@Valid @RequestBody StockOutOrderCreateRequest request) {
        return Result.success(stockOutOrderService.create(request));
    }

    @Operation(summary = "审核出库单")
    @PreAuthorize("hasAuthority('stock:approve')")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        stockOutOrderService.approve(id);
        return Result.success();
    }

    @Operation(summary = "驳回出库单")
    @PreAuthorize("hasAuthority('stock:approve')")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        stockOutOrderService.reject(id);
        return Result.success();
    }

    @Operation(summary = "删除出库单")
    @PreAuthorize("hasAuthority('stock:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stockOutOrderService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取出库单详情")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/{id}")
    public Result<StockOutOrder> getById(@PathVariable Long id) {
        return Result.success(stockOutOrderService.getById(id));
    }

    @Operation(summary = "根据仓库查询出库单")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<StockOutOrder>> getByWarehouseId(@PathVariable Long warehouseId) {
        return Result.success(stockOutOrderService.getByWarehouseId(warehouseId));
    }

    @Operation(summary = "分页查询出库单")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/page")
    public Result<PageResult<StockOutOrder>> getPage(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<StockOutOrder> page = stockOutOrderService.getPage(warehouseId, pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize));
    }
}
