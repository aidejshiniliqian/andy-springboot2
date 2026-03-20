package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.organization.*;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.OrganizationRepository;
import com.andy.warehouse.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public OrganizationDTO createOrganization(OrganizationCreateRequest request) {
        if (organizationRepository.existsByOrgCode(request.getOrgCode())) {
            throw new BusinessException("机构编码已存在");
        }

        Organization org = new Organization();
        BeanUtils.copyProperties(request, org);
        org.setStatus(1);

        Organization savedOrg = organizationRepository.save(org);
        return convertToDTO(savedOrg);
    }

    @Override
    @Transactional
    public OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));

        if (StringUtils.hasText(request.getOrgName())) {
            org.setOrgName(request.getOrgName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            org.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getAddress())) {
            org.setAddress(request.getAddress());
        }
        if (StringUtils.hasText(request.getContactPerson())) {
            org.setContactPerson(request.getContactPerson());
        }
        if (StringUtils.hasText(request.getContactPhone())) {
            org.setContactPhone(request.getContactPhone());
        }
        if (request.getStatus() != null) {
            org.setStatus(request.getStatus());
        }

        Organization updatedOrg = organizationRepository.save(org);
        return convertToDTO(updatedOrg);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));
        org.setIsDeleted(true);
        organizationRepository.save(org);
    }

    @Override
    public OrganizationDTO getOrganizationById(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));
        return convertToDTO(org);
    }

    @Override
    public PageResult<OrganizationDTO> getOrganizationList(OrganizationQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Organization> orgPage = organizationRepository.findByConditions(
                request.getOrgCode(),
                request.getOrgName(),
                request.getStatus(),
                pageable
        );
        return PageResult.of(orgPage.map(this::convertToDTO));
    }

    @Override
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .filter(org -> !Boolean.TRUE.equals(org.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrganizationStatus(Long id, Integer status) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));
        org.setStatus(status);
        organizationRepository.save(org);
    }

    private OrganizationDTO convertToDTO(Organization org) {
        OrganizationDTO dto = new OrganizationDTO();
        BeanUtils.copyProperties(org, dto);
        return dto;
    }
}
