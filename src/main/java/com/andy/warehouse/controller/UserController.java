package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.*;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "创建用户")
    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping
    public Result<User> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.create(request));
    }

    @Operation(summary = "更新用户")
    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping
    public Result<User> update(@Valid @RequestBody UserUpdateRequest request) {
        return Result.success(userService.update(request));
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "分页查询用户")
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/page")
    public Result<PageResult<User>> getPage(
            @RequestParam(required = false) Long orgId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<User> page = userService.getPage(orgId, pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @Operation(summary = "根据角色查询用户")
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/role/{roleId}")
    public Result<List<User>> getByRoleId(@PathVariable Long roleId) {
        return Result.success(userService.getByRoleId(roleId));
    }

    @Operation(summary = "修改密码")
    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping("/{id}/password")
    public Result<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordRequest request) {
        userService.changePassword(id, request);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @Operation(summary = "分配角色")
    @PreAuthorize("hasAuthority('user:update')")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    @Operation(summary = "获取用户角色")
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/{id}/roles")
    public Result<Set<Role>> getUserRoles(@PathVariable Long id) {
        return Result.success(userService.getUserRoles(id));
    }

    @Operation(summary = "获取用户权限")
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/{id}/permissions")
    public Result<Set<Permission>> getUserPermissions(@PathVariable Long id) {
        return Result.success(userService.getUserPermissions(id));
    }
}
