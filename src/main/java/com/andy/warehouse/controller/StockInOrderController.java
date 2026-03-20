package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.StockInOrderCreateRequest;
import com.andy.warehouse.entity.StockInOrder;
import com.andy.warehouse.service.StockInOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "入库管理")
@RestController
@RequestMapping("/stock-in-orders")
@RequiredArgsConstructor
public class StockInOrderController {

    private final StockInOrderService stockInOrderService;

    @Operation(summary = "创建入库单")
    @PreAuthorize("hasAuthority('stock:create')")
    @PostMapping
    public Result<StockInOrder> create(@Valid @RequestBody StockInOrderCreateRequest request) {
        return Result.success(stockInOrderService.create(request));
    }

    @Operation(summary = "审核入库单")
    @PreAuthorize("hasAuthority('stock:approve')")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        stockInOrderService.approve(id);
        return Result.success();
    }

    @Operation(summary = "驳回入库单")
    @PreAuthorize("hasAuthority('stock:approve')")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        stockInOrderService.reject(id);
        return Result.success();
    }

    @Operation(summary = "删除入库单")
    @PreAuthorize("hasAuthority('stock:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stockInOrderService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取入库单详情")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/{id}")
    public Result<StockInOrder> getById(@PathVariable Long id) {
        return Result.success(stockInOrderService.getById(id));
    }

    @Operation(summary = "根据仓库查询入库单")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<StockInOrder>> getByWarehouseId(@PathVariable Long warehouseId) {
        return Result.success(stockInOrderService.getByWarehouseId(warehouseId));
    }

    @Operation(summary = "分页查询入库单")
    @PreAuthorize("hasAuthority('stock:view')")
    @GetMapping("/page")
    public Result<PageResult<StockInOrder>> getPage(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<StockInOrder> page = stockInOrderService.getPage(warehouseId, pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize));
    }
}
