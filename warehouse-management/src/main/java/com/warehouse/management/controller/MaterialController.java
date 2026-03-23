package com.warehouse.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Material;
import com.warehouse.management.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public Result<Material> create(@RequestBody Material material) {
        if (materialService.existsByCode(material.getCode())) {
            return Result.error("物资编码已存在");
        }
        return Result.success(materialService.save(material));
    }

    @PutMapping("/{id}")
    public Result<Material> update(@PathVariable Long id, @RequestBody Material material) {
        return materialService.findById(id)
                .map(existing -> {
                    material.setId(id);
                    return Result.success(materialService.save(material));
                })
                .orElse(Result.error("物资不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (materialService.findById(id).isEmpty()) {
            return Result.error("物资不存在");
        }
        materialService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Material> findById(@PathVariable Long id) {
        return materialService.findById(id)
                .map(Result::success)
                .orElse(Result.error("物资不存在"));
    }

    @GetMapping
    public Result<List<Material>> findAll() {
        return Result.success(materialService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<Material>> findPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Material> pageable = new Page<>(page, size);
        return Result.success(materialService.findAll(pageable));
    }
}
