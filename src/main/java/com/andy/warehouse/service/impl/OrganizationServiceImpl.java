package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.OrganizationCreateRequest;
import com.andy.warehouse.dto.OrganizationUpdateRequest;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.mapper.OrganizationMapper;
import com.andy.warehouse.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public Organization create(OrganizationCreateRequest request) {
        if (request.getCode() != null && organizationMapper.existsByCode(request.getCode())) {
            throw new BusinessException("组织编码已存在");
        }
        Organization org = new Organization();
        org.setName(request.getName());
        org.setCode(request.getCode());
        org.setDescription(request.getDescription());
        org.setStatus(request.getStatus());
        org.setParentId(request.getParentId());
        organizationMapper.insert(org);
        return org;
    }

    @Override
    @Transactional
    public Organization update(OrganizationUpdateRequest request) {
        Organization org = organizationMapper.selectById(request.getId());
        if (org == null || org.getDeleted()) {
            throw new BusinessException("组织机构不存在");
        }
        if (request.getName() != null) {
            org.setName(request.getName());
        }
        if (request.getDescription() != null) {
            org.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            org.setStatus(request.getStatus());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(org.getId())) {
                throw new BusinessException("不能将自己设置为父级");
            }
            org.setParentId(request.getParentId());
        }
        organizationMapper.updateById(org);
        return org;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new BusinessException("组织机构不存在");
        }
        List<Organization> children = organizationMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子组织，无法删除");
        }
        org.setDeleted(true);
        organizationMapper.updateById(org);
    }

    @Override
    public Organization getById(Long id) {
        Organization org = organizationMapper.selectById(id);
        if (org == null || org.getDeleted()) {
            throw new BusinessException("组织机构不存在");
        }
        return org;
    }

    @Override
    public List<Organization> getAll() {
        return organizationMapper.findAllActive();
    }

    @Override
    public List<Organization> getRootOrganizations() {
        return organizationMapper.findRootOrganizations();
    }

    @Override
    public List<Organization> getChildren(Long parentId) {
        return organizationMapper.findByParentId(parentId);
    }

    @Override
    public List<Organization> getTree() {
        List<Organization> all = organizationMapper.findAllActive();
        Map<Long, Organization> map = new HashMap<>();
        List<Organization> roots = new ArrayList<>();
        for (Organization org : all) {
            map.put(org.getId(), org);
            org.setChildren(new ArrayList<>());
        }
        for (Organization org : all) {
            if (org.getParentId() == null) {
                roots.add(org);
            } else {
                Organization parent = map.get(org.getParentId());
                if (parent != null) {
                    parent.getChildren().add(org);
                }
            }
        }
        return roots;
    }
}
