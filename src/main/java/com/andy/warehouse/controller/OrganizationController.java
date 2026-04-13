package com.andy.warehouse.controller;

import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.OrganizationCreateRequest;
import com.andy.warehouse.dto.OrganizationUpdateRequest;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "组织机构管理")
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "创建组织")
    @PreAuthorize("hasAuthority('org:create')")
    @PostMapping
    public Result<Organization> create(@Valid @RequestBody OrganizationCreateRequest request) {
        return Result.success(organizationService.create(request));
    }

    @Operation(summary = "更新组织")
    @PreAuthorize("hasAuthority('org:update')")
    @PutMapping
    public Result<Organization> update(@Valid @RequestBody OrganizationUpdateRequest request) {
        return Result.success(organizationService.update(request));
    }

    @Operation(summary = "删除组织")
    @PreAuthorize("hasAuthority('org:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取组织详情")
    @PreAuthorize("hasAuthority('org:view')")
    @GetMapping("/{id}")
    public Result<Organization> getById(@PathVariable Long id) {
        return Result.success(organizationService.getById(id));
    }

    @Operation(summary = "获取所有组织")
    @PreAuthorize("hasAuthority('org:view')")
    @GetMapping
    public Result<List<Organization>> getAll() {
        return Result.success(organizationService.getAll());
    }

    @Operation(summary = "获取根组织")
    @PreAuthorize("hasAuthority('org:view')")
    @GetMapping("/roots")
    public Result<List<Organization>> getRootOrganizations() {
        return Result.success(organizationService.getRootOrganizations());
    }

    @Operation(summary = "获取子组织")
    @PreAuthorize("hasAuthority('org:view')")
    @GetMapping("/{id}/children")
    public Result<List<Organization>> getChildren(@PathVariable Long id) {
        return Result.success(organizationService.getChildren(id));
    }

    @Operation(summary = "获取组织树")
    @PreAuthorize("hasAuthority('org:view')")
    @GetMapping("/tree")
    public Result<List<Organization>> getTree() {
        return Result.success(organizationService.getTree());
    }
}
