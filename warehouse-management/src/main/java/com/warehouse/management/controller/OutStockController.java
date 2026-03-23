package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.OutStock;
import com.warehouse.management.service.OutStockService;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/out-stock")
@RequiredArgsConstructor
public class OutStockController {

    private final OutStockService outStockService;

    @PostMapping
    public Result<OutStock> create(@RequestBody OutStock outStock) {
        if (outStockService.existsByOrderNo(outStock.getOrderNo())) {
            return Result.error("出库单号已存在");
        }
        try {
            return Result.success(outStockService.createOutStock(outStock));
        } catch (Exception e) {
            return Result.error("出库失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<OutStock> update(@PathVariable Long id, @RequestBody OutStock outStock) {
        return outStockService.findById(id)
                .map(existing -> {
                    outStock.setId(id);
                    return Result.success(outStockService.save(outStock));
                })
                .orElse(Result.error("出库单不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (outStockService.findById(id).isEmpty()) {
            return Result.error("出库单不存在");
        }
        outStockService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<OutStock> findById(@PathVariable Long id) {
        return outStockService.findById(id)
                .map(Result::success)
                .orElse(Result.error("出库单不存在"));
    }

    @GetMapping
    public Result<List<OutStock>> findAll() {
        return Result.success(outStockService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<OutStock>> findPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OutStock> pageable = new Page<>(page, size);
        return Result.success(outStockService.findAll(pageable));
    }
}
