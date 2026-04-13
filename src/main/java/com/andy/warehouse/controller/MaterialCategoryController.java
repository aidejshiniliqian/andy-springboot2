package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.MaterialCategoryCreateRequest;
import com.andy.warehouse.dto.MaterialCategoryUpdateRequest;
import com.andy.warehouse.entity.MaterialCategory;
import com.andy.warehouse.service.MaterialCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物资分类管理")
@RestController
@RequestMapping("/material-categories")
@RequiredArgsConstructor
public class MaterialCategoryController {

    private final MaterialCategoryService categoryService;

    @Operation(summary = "创建分类")
    @PreAuthorize("hasAuthority('material:create')")
    @PostMapping
    public Result<MaterialCategory> create(@Valid @RequestBody MaterialCategoryCreateRequest request) {
        return Result.success(categoryService.create(request));
    }

    @Operation(summary = "更新分类")
    @PreAuthorize("hasAuthority('material:update')")
    @PutMapping
    public Result<MaterialCategory> update(@Valid @RequestBody MaterialCategoryUpdateRequest request) {
        return Result.success(categoryService.update(request));
    }

    @Operation(summary = "删除分类")
    @PreAuthorize("hasAuthority('material:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取分类详情")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/{id}")
    public Result<MaterialCategory> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @Operation(summary = "获取所有分类")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping
    public Result<List<MaterialCategory>> getAll() {
        return Result.success(categoryService.getAll());
    }

    @Operation(summary = "获取根分类")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/roots")
    public Result<List<MaterialCategory>> getRootCategories() {
        return Result.success(categoryService.getRootCategories());
    }

    @Operation(summary = "获取子分类")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/{id}/children")
    public Result<List<MaterialCategory>> getChildren(@PathVariable Long id) {
        return Result.success(categoryService.getChildren(id));
    }

    @Operation(summary = "获取分类树")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/tree")
    public Result<List<MaterialCategory>> getTree() {
        return Result.success(categoryService.getTree());
    }
}
