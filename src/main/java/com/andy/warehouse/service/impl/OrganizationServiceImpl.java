package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.organization.*;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.OrganizationMapper;
import com.andy.warehouse.service.OrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public OrganizationDTO createOrganization(OrganizationCreateRequest request) {
        if (organizationMapper.existsByOrgCode(request.getOrgCode())) {
            throw new BusinessException("机构编码已存在");
        }

        Organization org = new Organization();
        BeanUtils.copyProperties(request, org);
        org.setStatus(1);

        organizationMapper.insert(org);
        return convertToDTO(org);
    }

    @Override
    @Transactional
    public OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new ResourceNotFoundException("组织机构不存在");
        }

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

        organizationMapper.updateById(org);
        return convertToDTO(org);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new ResourceNotFoundException("组织机构不存在");
        }
        organizationMapper.deleteById(id);
    }

    @Override
    public OrganizationDTO getOrganizationById(Long id) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new ResourceNotFoundException("组织机构不存在");
        }
        return convertToDTO(org);
    }

    @Override
    public PageResult<OrganizationDTO> getOrganizationList(OrganizationQueryRequest request) {
        Page<Organization> page = new Page<>(request.getPage(), request.getSize());
        IPage<Organization> orgPage = organizationMapper.findByConditions(
                page,
                request.getOrgCode(),
                request.getOrgName(),
                request.getStatus()
        );
        List<OrganizationDTO> dtoList = orgPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, orgPage.getTotal(), orgPage.getCurrent(), orgPage.getSize());
    }

    @Override
    public List<OrganizationDTO> getAllOrganizations() {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getIsDeleted, false);
        return organizationMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrganizationStatus(Long id, Integer status) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new ResourceNotFoundException("组织机构不存在");
        }
        org.setStatus(status);
        organizationMapper.updateById(org);
    }

    private OrganizationDTO convertToDTO(Organization org) {
        OrganizationDTO dto = new OrganizationDTO();
        BeanUtils.copyProperties(org, dto);
        return dto;
    }
}
