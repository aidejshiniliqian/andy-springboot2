package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.OrganizationCreateRequest;
import com.andy.warehouse.dto.OrganizationUpdateRequest;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.repository.OrganizationRepository;
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

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Organization create(OrganizationCreateRequest request) {
        if (request.getCode() != null && organizationRepository.existsByCode(request.getCode())) {
            throw new BusinessException("组织编码已存在");
        }
        Organization org = new Organization();
        org.setName(request.getName());
        org.setCode(request.getCode());
        org.setDescription(request.getDescription());
        org.setStatus(request.getStatus());
        if (request.getParentId() != null) {
            Organization parent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级组织不存在"));
            org.setParent(parent);
        }
        return organizationRepository.save(org);
    }

    @Override
    @Transactional
    public Organization update(OrganizationUpdateRequest request) {
        Organization org = organizationRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("组织机构不存在"));
        if (org.getDeleted()) {
            throw new BusinessException("组织机构已被删除");
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
            Organization parent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级组织不存在"));
            org.setParent(parent);
        }
        return organizationRepository.save(org);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("组织机构不存在"));
        List<Organization> children = organizationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子组织，无法删除");
        }
        org.setDeleted(true);
        organizationRepository.save(org);
    }

    @Override
    public Organization getById(Long id) {
        return organizationRepository.findById(id)
                .filter(o -> !o.getDeleted())
                .orElseThrow(() -> new BusinessException("组织机构不存在"));
    }

    @Override
    public List<Organization> getAll() {
        return organizationRepository.findAllActive();
    }

    @Override
    public List<Organization> getRootOrganizations() {
        return organizationRepository.findRootOrganizations();
    }

    @Override
    public List<Organization> getChildren(Long parentId) {
        return organizationRepository.findByParentId(parentId);
    }

    @Override
    public List<Organization> getTree() {
        List<Organization> all = organizationRepository.findAllActive();
        Map<Long, Organization> map = new HashMap<>();
        List<Organization> roots = new ArrayList<>();
        for (Organization org : all) {
            map.put(org.getId(), org);
            org.setChildren(new ArrayList<>());
        }
        for (Organization org : all) {
            if (org.getParent() == null) {
                roots.add(org);
            } else {
                Organization parent = map.get(org.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(org);
                }
            }
        }
        return roots;
    }
}
