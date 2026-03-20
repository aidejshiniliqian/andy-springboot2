package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Permission;
import com.warehouse.management.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public Result<Permission> create(@RequestBody Permission permission) {
        return Result.success(permissionService.save(permission));
    }

    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        return permissionService.findById(id)
                .map(existing -> {
                    permission.setId(id);
                    return Result.success(permissionService.save(permission));
                })
                .orElse(Result.error("权限不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (permissionService.findById(id).isEmpty()) {
            return Result.error("权限不存在");
        }
        permissionService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Permission> findById(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(Result::success)
                .orElse(Result.error("权限不存在"));
    }

    @GetMapping
    public Result<List<Permission>> findAll() {
        return Result.success(permissionService.findAll());
    }

    @GetMapping("/root")
    public Result<List<Permission>> findRootPermissions() {
        return Result.success(permissionService.findRootPermissions());
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<Permission>> findByParentId(@PathVariable Long parentId) {
        return Result.success(permissionService.findByParentId(parentId));
    }

    @GetMapping("/page")
    public Result<Page<Permission>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(permissionService.findAll(pageable));
    }
}
