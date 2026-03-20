package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.role.*;
import com.andy.warehouse.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public Result<RoleDTO> createRole(@Valid @RequestBody RoleCreateRequest request) {
        return Result.success("创建成功", roleService.createRole(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<RoleDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return Result.success("更新成功", roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public Result<RoleDTO> getRoleById(@PathVariable Long id) {
        return Result.success(roleService.getRoleById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public Result<PageResult<RoleDTO>> getRoleList(RoleQueryRequest request) {
        return Result.success(roleService.getRoleList(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:read')")
    public Result<List<RoleDTO>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.updateRoleStatus(id, status);
        return Result.success("状态更新成功");
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success("权限分配成功");
    }
}
