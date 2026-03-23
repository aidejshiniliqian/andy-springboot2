package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.WarehouseCreateRequest;
import com.andy.warehouse.dto.WarehouseUpdateRequest;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.mapper.OrganizationMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.service.WarehouseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public Warehouse create(WarehouseCreateRequest request) {
        if (request.getCode() != null && warehouseMapper.existsByCode(request.getCode())) {
            throw new BusinessException("仓库编码已存在");
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setCode(request.getCode());
        warehouse.setAddress(request.getAddress());
        warehouse.setArea(request.getArea());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setManagerId(request.getManagerId());
        warehouse.setManagerName(request.getManagerName());
        warehouse.setPhone(request.getPhone());
        warehouse.setDescription(request.getDescription());
        warehouse.setStatus(request.getStatus());
        warehouse.setOrgId(request.getOrgId());
        warehouseMapper.insert(warehouse);
        return warehouse;
    }

    @Override
    @Transactional
    public Warehouse update(WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(request.getId());
        if (warehouse == null || warehouse.getDeleted()) {
            throw new BusinessException("仓库不存在");
        }
        if (request.getName() != null) {
            warehouse.setName(request.getName());
        }
        if (request.getAddress() != null) {
            warehouse.setAddress(request.getAddress());
        }
        if (request.getArea() != null) {
            warehouse.setArea(request.getArea());
        }
        if (request.getCapacity() != null) {
            warehouse.setCapacity(request.getCapacity());
        }
        if (request.getManagerId() != null) {
            warehouse.setManagerId(request.getManagerId());
        }
        if (request.getManagerName() != null) {
            warehouse.setManagerName(request.getManagerName());
        }
        if (request.getPhone() != null) {
            warehouse.setPhone(request.getPhone());
        }
        if (request.getDescription() != null) {
            warehouse.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            warehouse.setStatus(request.getStatus());
        }
        warehouseMapper.updateById(warehouse);
        return warehouse;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        warehouse.setDeleted(true);
        warehouseMapper.updateById(warehouse);
    }

    @Override
    public Warehouse getById(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null || warehouse.getDeleted()) {
            throw new BusinessException("仓库不存在");
        }
        return warehouse;
    }

    @Override
    public List<Warehouse> getAll() {
        return warehouseMapper.findAllActive();
    }

    @Override
    public Page<Warehouse> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword) {
        Page<Warehouse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getDeleted, false);
        if (orgId != null) {
            wrapper.eq(Warehouse::getOrgId, orgId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Warehouse::getName, keyword).or().like(Warehouse::getCode, keyword));
        }
        wrapper.orderByDesc(Warehouse::getCreatedAt);
        IPage<Warehouse> warehousePage = warehouseMapper.selectPage(page, wrapper);
        return (Page<Warehouse>) warehousePage;
    }
}
