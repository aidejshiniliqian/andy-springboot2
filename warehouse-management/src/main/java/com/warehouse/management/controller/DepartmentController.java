package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Department;
import com.warehouse.management.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public Result<Department> create(@RequestBody Department department) {
        return Result.success(departmentService.save(department));
    }

    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id, @RequestBody Department department) {
        return departmentService.findById(id)
                .map(existing -> {
                    department.setId(id);
                    return Result.success(departmentService.save(department));
                })
                .orElse(Result.error("部门不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (departmentService.findById(id).isEmpty()) {
            return Result.error("部门不存在");
        }
        departmentService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Department> findById(@PathVariable Long id) {
        return departmentService.findById(id)
                .map(Result::success)
                .orElse(Result.error("部门不存在"));
    }

    @GetMapping
    public Result<List<Department>> findAll() {
        return Result.success(departmentService.findAll());
    }

    @GetMapping("/root")
    public Result<List<Department>> findRootDepartments() {
        return Result.success(departmentService.findRootDepartments());
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<Department>> findByParentId(@PathVariable Long parentId) {
        return Result.success(departmentService.findByParentId(parentId));
    }

    @GetMapping("/page")
    public Result<Page<Department>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(departmentService.findAll(pageable));
    }
}
