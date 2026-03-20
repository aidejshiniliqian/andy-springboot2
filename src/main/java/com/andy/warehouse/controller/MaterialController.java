package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.material.*;
import com.andy.warehouse.service.MaterialCategoryService;
import com.andy.warehouse.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialCategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('material:create')")
    public Result<MaterialDTO> createMaterial(@Valid @RequestBody MaterialCreateRequest request) {
        return Result.success("创建成功", materialService.createMaterial(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('material:update')")
    public Result<MaterialDTO> updateMaterial(@PathVariable Long id, @Valid @RequestBody MaterialUpdateRequest request) {
        return Result.success("更新成功", materialService.updateMaterial(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('material:delete')")
    public Result<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<MaterialDTO> getMaterialById(@PathVariable Long id) {
        return Result.success(materialService.getMaterialById(id));
    }

    @GetMapping("/by-code/{materialCode}")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<MaterialDTO> getMaterialByCode(@PathVariable String materialCode) {
        return Result.success(materialService.getMaterialByCode(materialCode));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('material:read')")
    public Result<PageResult<MaterialDTO>> getMaterialList(MaterialQueryRequest request) {
        return Result.success(materialService.getMaterialList(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<List<MaterialDTO>> getAllMaterials() {
        return Result.success(materialService.getAllMaterials());
    }

    @GetMapping("/by-category/{categoryId}")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<List<MaterialDTO>> getMaterialsByCategoryId(@PathVariable Long categoryId) {
        return Result.success(materialService.getMaterialsByCategoryId(categoryId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('material:update')")
    public Result<Void> updateMaterialStatus(@PathVariable Long id, @RequestParam Integer status) {
        materialService.updateMaterialStatus(id, status);
        return Result.success("状态更新成功");
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('material:create')")
    public Result<MaterialCategoryDTO> createCategory(@Valid @RequestBody MaterialCategoryCreateRequest request) {
        return Result.success("创建成功", categoryService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('material:update')")
    public Result<MaterialCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody MaterialCategoryUpdateRequest request) {
        return Result.success("更新成功", categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('material:delete')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }

    @GetMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<MaterialCategoryDTO> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @GetMapping("/categories/tree")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<List<MaterialCategoryDTO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/categories/all")
    @PreAuthorize("hasAuthority('material:read')")
    public Result<List<MaterialCategoryDTO>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }
}
