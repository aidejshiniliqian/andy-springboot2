package com.andy.warehouse.service;

import com.andy.warehouse.dto.OrganizationCreateRequest;
import com.andy.warehouse.dto.OrganizationUpdateRequest;
import com.andy.warehouse.entity.Organization;

import java.util.List;

public interface OrganizationService {

    Organization create(OrganizationCreateRequest request);

    Organization update(OrganizationUpdateRequest request);

    void delete(Long id);

    Organization getById(Long id);

    List<Organization> getAll();

    List<Organization> getRootOrganizations();

    List<Organization> getChildren(Long parentId);

    List<Organization> getTree();
}
