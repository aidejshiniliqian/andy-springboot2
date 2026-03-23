package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Organization;
import com.warehouse.management.mapper.OrganizationMapper;
import com.warehouse.management.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Override
    public Organization save(Organization organization) {
        this.saveOrUpdate(organization);
        return organization;
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return Optional.ofNullable(this.getById(id));
    }

    @Override
    public List<Organization> findAll() {
        return this.list();
    }

    @Override
    public Page<Organization> findAll(Page<Organization> pageable) {
        return this.page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }
}
