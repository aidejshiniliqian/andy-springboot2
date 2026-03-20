package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.department.DepartmentCreateRequest;
import com.andy.warehouse.dto.department.DepartmentDTO;
import com.andy.warehouse.dto.department.DepartmentUpdateRequest;
import com.andy.warehouse.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('dept:create')")
    public Result<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        return Result.success("创建成功", departmentService.createDepartment(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:update')")
    public Result<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        return Result.success("更新成功", departmentService.updateDepartment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:delete')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:read')")
    public Result<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return Result.success(departmentService.getDepartmentById(id));
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('dept:read')")
    public Result<List<DepartmentDTO>> getDepartmentTree(@RequestParam Long orgId) {
        return Result.success(departmentService.getDepartmentTree(orgId));
    }

    @GetMapping("/by-org/{orgId}")
    @PreAuthorize("hasAuthority('dept:read')")
    public Result<List<DepartmentDTO>> getDepartmentsByOrgId(@PathVariable Long orgId) {
        return Result.success(departmentService.getDepartmentsByOrgId(orgId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('dept:update')")
    public Result<Void> updateDepartmentStatus(@PathVariable Long id, @RequestParam Integer status) {
        departmentService.updateDepartmentStatus(id, status);
        return Result.success("状态更新成功");
    }
}
