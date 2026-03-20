package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Organization;
import com.warehouse.management.repository.OrganizationRepository;
import com.warehouse.management.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return organizationRepository.findById(id);
    }

    @Override
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    @Override
    public List<Organization> findRootOrganizations() {
        return organizationRepository.findByParentIdIsNull();
    }

    @Override
    public List<Organization> findByParentId(Long parentId) {
        return organizationRepository.findByParentId(parentId);
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        organizationRepository.deleteById(id);
    }
}
