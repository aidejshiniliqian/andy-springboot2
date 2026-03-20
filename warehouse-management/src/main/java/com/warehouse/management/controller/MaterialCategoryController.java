package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.MaterialCategory;
import com.warehouse.management.service.MaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material-categories")
@RequiredArgsConstructor
public class MaterialCategoryController {

    private final MaterialCategoryService categoryService;

    @PostMapping
    public Result<MaterialCategory> create(@RequestBody MaterialCategory category) {
        if (categoryService.existsByCode(category.getCode())) {
            return Result.error("分类编码已存在");
        }
        return Result.success(categoryService.save(category));
    }

    @PutMapping("/{id}")
    public Result<MaterialCategory> update(@PathVariable Long id, @RequestBody MaterialCategory category) {
        return categoryService.findById(id)
                .map(existing -> {
                    category.setId(id);
                    return Result.success(categoryService.save(category));
                })
                .orElse(Result.error("物资分类不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (categoryService.findById(id).isEmpty()) {
            return Result.error("物资分类不存在");
        }
        categoryService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MaterialCategory> findById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(Result::success)
                .orElse(Result.error("物资分类不存在"));
    }

    @GetMapping
    public Result<List<MaterialCategory>> findAll() {
        return Result.success(categoryService.findAll());
    }

    @GetMapping("/root")
    public Result<List<MaterialCategory>> findRootCategories() {
        return Result.success(categoryService.findRootCategories());
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<MaterialCategory>> findByParentId(@PathVariable Long parentId) {
        return Result.success(categoryService.findByParentId(parentId));
    }

    @GetMapping("/page")
    public Result<Page<MaterialCategory>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(categoryService.findAll(pageable));
    }
}
