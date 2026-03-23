package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.warehouse.*;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.UserMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.service.WarehouseService;
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
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseCreateRequest request) {
        if (warehouseMapper.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new BusinessException("仓库编码已存在");
        }

        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(request, warehouse);
        warehouse.setStatus(1);

        warehouseMapper.insert(warehouse);
        return convertToDTO(warehouse);
    }

    @Override
    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }

        if (StringUtils.hasText(request.getWarehouseName())) {
            warehouse.setWarehouseName(request.getWarehouseName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            warehouse.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getAddress())) {
            warehouse.setAddress(request.getAddress());
        }
        if (request.getArea() != null) {
            warehouse.setArea(request.getArea());
        }
        if (request.getCapacity() != null) {
            warehouse.setCapacity(request.getCapacity());
        }
        if (StringUtils.hasText(request.getContactPhone())) {
            warehouse.setContactPhone(request.getContactPhone());
        }
        if (request.getStatus() != null) {
            warehouse.setStatus(request.getStatus());
        }
        if (request.getManagerId() != null) {
            warehouse.setManagerId(request.getManagerId());
        }

        warehouseMapper.updateById(warehouse);
        return convertToDTO(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }
        warehouseMapper.deleteById(id);
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }
        return convertToDTO(warehouse);
    }

    @Override
    public PageResult<WarehouseDTO> getWarehouseList(WarehouseQueryRequest request) {
        Page<Warehouse> page = new Page<>(request.getPage(), request.getSize());
        IPage<Warehouse> warehousePage = warehouseMapper.findByConditions(
                page,
                request.getWarehouseCode(),
                request.getWarehouseName(),
                request.getStatus()
        );
        List<WarehouseDTO> dtoList = warehousePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, warehousePage.getTotal(), warehousePage.getCurrent(), warehousePage.getSize());
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses() {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getIsDeleted, false);
        return warehouseMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateWarehouseStatus(Long id, Integer status) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }
        warehouse.setStatus(status);
        warehouseMapper.updateById(warehouse);
    }

    private WarehouseDTO convertToDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        BeanUtils.copyProperties(warehouse, dto);
        if (warehouse.getManagerId() != null) {
            dto.setManagerId(warehouse.getManagerId());
            User manager = userMapper.selectById(warehouse.getManagerId());
            if (manager != null) {
                dto.setManagerName(manager.getRealName());
            }
        }
        return dto;
    }
}
