package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.RoleCreateRequest;
import com.andy.warehouse.dto.RoleUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.service.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "创建角色")
    @PreAuthorize("hasAuthority('role:create')")
    @PostMapping
    public Result<Role> create(@Valid @RequestBody RoleCreateRequest request) {
        return Result.success(roleService.create(request));
    }

    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping
    public Result<Role> update(@Valid @RequestBody RoleUpdateRequest request) {
        return Result.success(roleService.update(request));
    }

    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "获取所有角色")
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping
    public Result<List<Role>> getAll() {
        return Result.success(roleService.getAll());
    }

    @Operation(summary = "分页查询角色")
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/page")
    public Result<PageResult<Role>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Role> page = roleService.getPage(pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @Operation(summary = "分配权限")
    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }

    @Operation(summary = "获取角色权限")
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/{id}/permissions")
    public Result<Set<Permission>> getRolePermissions(@PathVariable Long id) {
        return Result.success(roleService.getRolePermissions(id));
    }
}
