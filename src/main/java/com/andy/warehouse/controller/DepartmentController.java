package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.DepartmentCreateRequest;
import com.andy.warehouse.dto.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "创建部门")
    @PreAuthorize("hasAuthority('dept:create')")
    @PostMapping
    public Result<Department> create(@Valid @RequestBody DepartmentCreateRequest request) {
        return Result.success(departmentService.create(request));
    }

    @Operation(summary = "更新部门")
    @PreAuthorize("hasAuthority('dept:update')")
    @PutMapping
    public Result<Department> update(@Valid @RequestBody DepartmentUpdateRequest request) {
        return Result.success(departmentService.update(request));
    }

    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('dept:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取部门详情")
    @PreAuthorize("hasAuthority('dept:view')")
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @Operation(summary = "根据组织查询部门")
    @PreAuthorize("hasAuthority('dept:view')")
    @GetMapping("/org/{orgId}")
    public Result<List<Department>> getByOrgId(@PathVariable Long orgId) {
        return Result.success(departmentService.getByOrgId(orgId));
    }

    @Operation(summary = "获取根部门")
    @PreAuthorize("hasAuthority('dept:view')")
    @GetMapping("/roots/{orgId}")
    public Result<List<Department>> getRootDepartments(@PathVariable Long orgId) {
        return Result.success(departmentService.getRootDepartments(orgId));
    }

    @Operation(summary = "获取子部门")
    @PreAuthorize("hasAuthority('dept:view')")
    @GetMapping("/{id}/children")
    public Result<List<Department>> getChildren(@PathVariable Long id) {
        return Result.success(departmentService.getChildren(id));
    }

    @Operation(summary = "获取部门树")
    @PreAuthorize("hasAuthority('dept:view')")
    @GetMapping("/tree/{orgId}")
    public Result<List<Department>> getTree(@PathVariable Long orgId) {
        return Result.success(departmentService.getTree(orgId));
    }
}
