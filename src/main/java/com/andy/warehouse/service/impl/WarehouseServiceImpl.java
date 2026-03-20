package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.warehouse.*;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.UserRepository;
import com.andy.warehouse.repository.WarehouseRepository;
import com.andy.warehouse.service.WarehouseService;
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
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new BusinessException("仓库编码已存在");
        }

        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(request, warehouse);
        warehouse.setStatus(1);

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("管理员不存在"));
            warehouse.setManager(manager);
        }

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return convertToDTO(savedWarehouse);
    }

    @Override
    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));

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
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("管理员不存在"));
            warehouse.setManager(manager);
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return convertToDTO(updatedWarehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));
        warehouse.setIsDeleted(true);
        warehouseRepository.save(warehouse);
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));
        return convertToDTO(warehouse);
    }

    @Override
    public PageResult<WarehouseDTO> getWarehouseList(WarehouseQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Warehouse> warehousePage = warehouseRepository.findByConditions(
                request.getWarehouseCode(),
                request.getWarehouseName(),
                request.getStatus(),
                pageable
        );
        return PageResult.of(warehousePage.map(this::convertToDTO));
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .filter(warehouse -> !Boolean.TRUE.equals(warehouse.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateWarehouseStatus(Long id, Integer status) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));
        warehouse.setStatus(status);
        warehouseRepository.save(warehouse);
    }

    private WarehouseDTO convertToDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        BeanUtils.copyProperties(warehouse, dto);
        if (warehouse.getManager() != null) {
            dto.setManagerId(warehouse.getManager().getId());
            dto.setManagerName(warehouse.getManager().getRealName());
        }
        return dto;
    }
}
