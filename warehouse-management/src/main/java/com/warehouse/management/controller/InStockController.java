package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.InStock;
import com.warehouse.management.service.InStockService;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/in-stock")
@RequiredArgsConstructor
public class InStockController {

    private final InStockService inStockService;

    @PostMapping
    public Result<InStock> create(@RequestBody InStock inStock) {
        if (inStockService.existsByOrderNo(inStock.getOrderNo())) {
            return Result.error("入库单号已存在");
        }
        try {
            return Result.success(inStockService.createInStock(inStock));
        } catch (Exception e) {
            return Result.error("入库失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<InStock> update(@PathVariable Long id, @RequestBody InStock inStock) {
        return inStockService.findById(id)
                .map(existing -> {
                    inStock.setId(id);
                    return Result.success(inStockService.save(inStock));
                })
                .orElse(Result.error("入库单不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (inStockService.findById(id).isEmpty()) {
            return Result.error("入库单不存在");
        }
        inStockService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<InStock> findById(@PathVariable Long id) {
        return inStockService.findById(id)
                .map(Result::success)
                .orElse(Result.error("入库单不存在"));
    }

    @GetMapping
    public Result<List<InStock>> findAll() {
        return Result.success(inStockService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<InStock>> findPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InStock> pageable = new Page<>(page, size);
        return Result.success(inStockService.findAll(pageable));
    }
}
