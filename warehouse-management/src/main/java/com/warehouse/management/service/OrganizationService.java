package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    Organization save(Organization organization);
    Optional<Organization> findById(Long id);
    List<Organization> findAll();
    List<Organization> findRootOrganizations();
    List<Organization> findByParentId(Long parentId);
    Page<Organization> findAll(Page<Organization> pageable);
    void deleteById(Long id);
}
