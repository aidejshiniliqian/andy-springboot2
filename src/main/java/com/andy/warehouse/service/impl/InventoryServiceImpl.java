package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.InventoryDTO;
import com.andy.warehouse.dto.stock.InventoryQueryRequest;
import com.andy.warehouse.dto.stock.InventoryRecordDTO;
import com.andy.warehouse.dto.stock.InventoryRecordQueryRequest;
import com.andy.warehouse.entity.Inventory;
import com.andy.warehouse.entity.InventoryRecord;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.User;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.entity.WarehouseLocation;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.InventoryMapper;
import com.andy.warehouse.mapper.InventoryRecordMapper;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.mapper.UserMapper;
import com.andy.warehouse.mapper.WarehouseLocationMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.service.InventoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final MaterialMapper materialMapper;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseLocationMapper locationMapper;
    private final UserMapper userMapper;

    @Override
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new ResourceNotFoundException("库存记录不存在");
        }
        return convertToDTO(inventory);
    }

    @Override
    public PageResult<InventoryDTO> getInventoryList(InventoryQueryRequest request) {
        Page<Inventory> page = new Page<>(request.getPage(), request.getSize());
        IPage<Inventory> inventoryPage = inventoryMapper.findByConditions(
                page,
                request.getMaterialId(),
                request.getWarehouseId(),
                request.getLocationId(),
                request.getBatchNo()
        );
        List<InventoryDTO> dtoList = inventoryPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, inventoryPage.getTotal(), inventoryPage.getCurrent(), inventoryPage.getSize());
    }

    @Override
    public List<InventoryDTO> getInventoryByMaterialId(Long materialId) {
        return inventoryMapper.findByMaterialId(materialId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> getInventoryByWarehouseId(Long warehouseId) {
        return inventoryMapper.findByWarehouseIdAndStatus(warehouseId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalQuantityByMaterialId(Long materialId) {
        BigDecimal total = inventoryMapper.getTotalQuantityByMaterialId(materialId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public PageResult<InventoryRecordDTO> getInventoryRecordList(InventoryRecordQueryRequest request) {
        Page<InventoryRecord> page = new Page<>(request.getPage(), request.getSize());
        IPage<InventoryRecord> recordPage = inventoryRecordMapper.findByConditions(
                page,
                request.getRecordNo(),
                request.getRecordType(),
                request.getMaterialId(),
                request.getWarehouseId(),
                request.getStartTime(),
                request.getEndTime()
        );
        List<InventoryRecordDTO> dtoList = recordPage.getRecords().stream()
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, recordPage.getTotal(), recordPage.getCurrent(), recordPage.getSize());
    }

    @Override
    public List<InventoryRecordDTO> getInventoryRecordsByMaterialAndWarehouse(Long materialId, Long warehouseId, int limit) {
        List<InventoryRecord> records = inventoryRecordMapper.findByMaterialAndWarehouse(materialId, warehouseId);
        return records.stream()
                .limit(limit)
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        BeanUtils.copyProperties(inventory, dto);
        if (inventory.getMaterialId() != null) {
            dto.setMaterialId(inventory.getMaterialId());
            Material material = materialMapper.selectById(inventory.getMaterialId());
            if (material != null) {
                dto.setMaterialCode(material.getMaterialCode());
                dto.setMaterialName(material.getMaterialName());
                dto.setSpecification(material.getSpecification());
            }
        }
        if (inventory.getWarehouseId() != null) {
            dto.setWarehouseId(inventory.getWarehouseId());
            Warehouse warehouse = warehouseMapper.selectById(inventory.getWarehouseId());
            if (warehouse != null) {
                dto.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        if (inventory.getLocationId() != null) {
            dto.setLocationId(inventory.getLocationId());
            WarehouseLocation location = locationMapper.selectById(inventory.getLocationId());
            if (location != null) {
                dto.setLocationCode(location.getLocationCode());
                dto.setLocationName(location.getLocationName());
            }
        }
        return dto;
    }

    private InventoryRecordDTO convertRecordToDTO(InventoryRecord record) {
        InventoryRecordDTO dto = new InventoryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        if (record.getMaterialId() != null) {
            dto.setMaterialId(record.getMaterialId());
            Material material = materialMapper.selectById(record.getMaterialId());
            if (material != null) {
                dto.setMaterialCode(material.getMaterialCode());
                dto.setMaterialName(material.getMaterialName());
            }
        }
        if (record.getWarehouseId() != null) {
            dto.setWarehouseId(record.getWarehouseId());
            Warehouse warehouse = warehouseMapper.selectById(record.getWarehouseId());
            if (warehouse != null) {
                dto.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        if (record.getLocationId() != null) {
            dto.setLocationId(record.getLocationId());
            WarehouseLocation location = locationMapper.selectById(record.getLocationId());
            if (location != null) {
                dto.setLocationCode(location.getLocationCode());
            }
        }
        if (record.getOperatorId() != null) {
            dto.setOperatorId(record.getOperatorId());
            User operator = userMapper.selectById(record.getOperatorId());
            if (operator != null) {
                dto.setOperatorName(operator.getRealName());
            }
        }
        return dto;
    }
}
