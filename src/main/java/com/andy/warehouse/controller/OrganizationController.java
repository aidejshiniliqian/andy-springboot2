package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.organization.*;
import com.andy.warehouse.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('org:create')")
    public Result<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationCreateRequest request) {
        return Result.success("创建成功", organizationService.createOrganization(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('org:update')")
    public Result<OrganizationDTO> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequest request) {
        return Result.success("更新成功", organizationService.updateOrganization(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('org:delete')")
    public Result<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('org:read')")
    public Result<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        return Result.success(organizationService.getOrganizationById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('org:read')")
    public Result<PageResult<OrganizationDTO>> getOrganizationList(OrganizationQueryRequest request) {
        return Result.success(organizationService.getOrganizationList(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('org:read')")
    public Result<List<OrganizationDTO>> getAllOrganizations() {
        return Result.success(organizationService.getAllOrganizations());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('org:update')")
    public Result<Void> updateOrganizationStatus(@PathVariable Long id, @RequestParam Integer status) {
        organizationService.updateOrganizationStatus(id, status);
        return Result.success("状态更新成功");
    }
}
