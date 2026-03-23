package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.permission.PermissionCreateRequest;
import com.andy.warehouse.dto.permission.PermissionDTO;
import com.andy.warehouse.dto.permission.PermissionUpdateRequest;
import com.andy.warehouse.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public Result<PermissionDTO> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        return Result.success("创建成功", permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public Result<PermissionDTO> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionUpdateRequest request) {
        return Result.success("更新成功", permissionService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public Result<PermissionDTO> getPermissionById(@PathVariable Long id) {
        return Result.success(permissionService.getPermissionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public Result<List<PermissionDTO>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:read')")
    public Result<List<PermissionDTO>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @GetMapping("/by-role/{roleId}")
    @PreAuthorize("hasAuthority('permission:read')")
    public Result<List<PermissionDTO>> getPermissionsByRoleId(@PathVariable Long roleId) {
        return Result.success(permissionService.getPermissionsByRoleId(roleId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('permission:update')")
    public Result<Void> updatePermissionStatus(@PathVariable Long id, @RequestParam Integer status) {
        permissionService.updatePermissionStatus(id, status);
        return Result.success("状态更新成功");
    }
}
