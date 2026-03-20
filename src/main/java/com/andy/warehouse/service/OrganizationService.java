package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.organization.*;

import java.util.List;

public interface OrganizationService {

    OrganizationDTO createOrganization(OrganizationCreateRequest request);

    OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request);

    void deleteOrganization(Long id);

    OrganizationDTO getOrganizationById(Long id);

    PageResult<OrganizationDTO> getOrganizationList(OrganizationQueryRequest request);

    List<OrganizationDTO> getAllOrganizations();

    void updateOrganizationStatus(Long id, Integer status);
}
