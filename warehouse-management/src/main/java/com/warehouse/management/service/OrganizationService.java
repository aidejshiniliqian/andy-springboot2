package com.warehouse.management.service;

import com.warehouse.management.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    Organization save(Organization organization);
    Optional<Organization> findById(Long id);
    List<Organization> findAll();
    List<Organization> findRootOrganizations();
    List<Organization> findByParentId(Long parentId);
    Page<Organization> findAll(Pageable pageable);
    void deleteById(Long id);
}
