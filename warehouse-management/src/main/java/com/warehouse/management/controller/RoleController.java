package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Role;
import com.warehouse.management.service.RoleService;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        if (roleService.existsByCode(role.getCode())) {
            return Result.error("角色编码已存在");
        }
        return Result.success(roleService.save(role));
    }

    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        return roleService.findById(id)
                .map(existing -> {
                    role.setId(id);
                    return Result.success(roleService.save(role));
                })
                .orElse(Result.error("角色不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (roleService.findById(id).isEmpty()) {
            return Result.error("角色不存在");
        }
        roleService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Role> findById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(Result::success)
                .orElse(Result.error("角色不存在"));
    }

    @GetMapping
    public Result<List<Role>> findAll() {
        return Result.success(roleService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<Role>> findPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Role> pageable = new Page<>(page, size);
        return Result.success(roleService.findAll(pageable));
    }
}
