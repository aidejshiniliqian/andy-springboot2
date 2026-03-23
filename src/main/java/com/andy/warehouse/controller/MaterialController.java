package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.MaterialCreateRequest;
import com.andy.warehouse.dto.MaterialUpdateRequest;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.service.MaterialService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物资管理")
@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "创建物资")
    @PreAuthorize("hasAuthority('material:create')")
    @PostMapping
    public Result<Material> create(@Valid @RequestBody MaterialCreateRequest request) {
        return Result.success(materialService.create(request));
    }

    @Operation(summary = "更新物资")
    @PreAuthorize("hasAuthority('material:update')")
    @PutMapping
    public Result<Material> update(@Valid @RequestBody MaterialUpdateRequest request) {
        return Result.success(materialService.update(request));
    }

    @Operation(summary = "删除物资")
    @PreAuthorize("hasAuthority('material:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取物资详情")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        return Result.success(materialService.getById(id));
    }

    @Operation(summary = "根据编码查询物资")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/code/{code}")
    public Result<Material> getByCode(@PathVariable String code) {
        return Result.success(materialService.getByCode(code));
    }

    @Operation(summary = "根据条码查询物资")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/barcode/{barcode}")
    public Result<Material> getByBarcode(@PathVariable String barcode) {
        return Result.success(materialService.getByBarcode(barcode));
    }

    @Operation(summary = "获取所有物资")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping
    public Result<List<Material>> getAll() {
        return Result.success(materialService.getAll());
    }

    @Operation(summary = "根据分类查询物资")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/category/{categoryId}")
    public Result<List<Material>> getByCategoryId(@PathVariable Long categoryId) {
        return Result.success(materialService.getByCategoryId(categoryId));
    }

    @Operation(summary = "分页查询物资")
    @PreAuthorize("hasAuthority('material:view')")
    @GetMapping("/page")
    public Result<PageResult<Material>> getPage(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Material> page = materialService.getPage(categoryId, pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }
}
