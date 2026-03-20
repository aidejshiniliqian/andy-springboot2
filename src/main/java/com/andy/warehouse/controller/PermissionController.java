package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.PermissionCreateRequest;
import com.andy.warehouse.dto.PermissionUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "创建权限")
    @PreAuthorize("hasAuthority('perm:create')")
    @PostMapping
    public Result<Permission> create(@Valid @RequestBody PermissionCreateRequest request) {
        return Result.success(permissionService.create(request));
    }

    @Operation(summary = "更新权限")
    @PreAuthorize("hasAuthority('perm:update')")
    @PutMapping
    public Result<Permission> update(@Valid @RequestBody PermissionUpdateRequest request) {
        return Result.success(permissionService.update(request));
    }

    @Operation(summary = "删除权限")
    @PreAuthorize("hasAuthority('perm:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取权限详情")
    @PreAuthorize("hasAuthority('perm:view')")
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @Operation(summary = "获取所有权限")
    @PreAuthorize("hasAuthority('perm:view')")
    @GetMapping
    public Result<List<Permission>> getAll() {
        return Result.success(permissionService.getAll());
    }

    @Operation(summary = "获取根权限")
    @PreAuthorize("hasAuthority('perm:view')")
    @GetMapping("/roots")
    public Result<List<Permission>> getRootPermissions() {
        return Result.success(permissionService.getRootPermissions());
    }

    @Operation(summary = "获取子权限")
    @PreAuthorize("hasAuthority('perm:view')")
    @GetMapping("/{id}/children")
    public Result<List<Permission>> getChildren(@PathVariable Long id) {
        return Result.success(permissionService.getChildren(id));
    }

    @Operation(summary = "获取权限树")
    @PreAuthorize("hasAuthority('perm:view')")
    @GetMapping("/tree")
    public Result<List<Permission>> getTree() {
        return Result.success(permissionService.getTree());
    }
}
