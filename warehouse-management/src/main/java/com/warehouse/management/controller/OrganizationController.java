package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.Organization;
import com.warehouse.management.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public Result<Organization> create(@RequestBody Organization organization) {
        return Result.success(organizationService.save(organization));
    }

    @PutMapping("/{id}")
    public Result<Organization> update(@PathVariable Long id, @RequestBody Organization organization) {
        return organizationService.findById(id)
                .map(existing -> {
                    organization.setId(id);
                    return Result.success(organizationService.save(organization));
                })
                .orElse(Result.error("组织机构不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (organizationService.findById(id).isEmpty()) {
            return Result.error("组织机构不存在");
        }
        organizationService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Organization> findById(@PathVariable Long id) {
        return organizationService.findById(id)
                .map(Result::success)
                .orElse(Result.error("组织机构不存在"));
    }

    @GetMapping
    public Result<List<Organization>> findAll() {
        return Result.success(organizationService.findAll());
    }

    @GetMapping("/root")
    public Result<List<Organization>> findRootOrganizations() {
        return Result.success(organizationService.findRootOrganizations());
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<Organization>> findByParentId(@PathVariable Long parentId) {
        return Result.success(organizationService.findByParentId(parentId));
    }

    @GetMapping("/page")
    public Result<Page<Organization>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(organizationService.findAll(pageable));
    }
}
