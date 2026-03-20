package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.InventoryDTO;
import com.andy.warehouse.dto.stock.InventoryQueryRequest;
import com.andy.warehouse.dto.stock.InventoryRecordDTO;
import com.andy.warehouse.dto.stock.InventoryRecordQueryRequest;
import com.andy.warehouse.entity.Inventory;
import com.andy.warehouse.entity.InventoryRecord;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.InventoryRecordRepository;
import com.andy.warehouse.repository.InventoryRepository;
import com.andy.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;

    @Override
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("库存记录不存在"));
        return convertToDTO(inventory);
    }

    @Override
    public PageResult<InventoryDTO> getInventoryList(InventoryQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Inventory> inventoryPage = inventoryRepository.findByConditions(
                request.getMaterialId(),
                request.getWarehouseId(),
                request.getLocationId(),
                request.getBatchNo(),
                pageable
        );
        return PageResult.of(inventoryPage.map(this::convertToDTO));
    }

    @Override
    public List<InventoryDTO> getInventoryByMaterialId(Long materialId) {
        return inventoryRepository.findByMaterialIdAndWarehouseId(materialId, null).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> getInventoryByWarehouseId(Long warehouseId) {
        return inventoryRepository.findByWarehouseIdAndStatus(warehouseId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalQuantityByMaterialId(Long materialId) {
        BigDecimal total = inventoryRepository.getTotalQuantityByMaterialId(materialId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public PageResult<InventoryRecordDTO> getInventoryRecordList(InventoryRecordQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<InventoryRecord> recordPage = inventoryRecordRepository.findByConditions(
                request.getRecordNo(),
                request.getRecordType(),
                request.getMaterialId(),
                request.getWarehouseId(),
                request.getStartTime(),
                request.getEndTime(),
                pageable
        );
        return PageResult.of(recordPage.map(this::convertRecordToDTO));
    }

    @Override
    public List<InventoryRecordDTO> getInventoryRecordsByMaterialAndWarehouse(Long materialId, Long warehouseId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<InventoryRecord> records = inventoryRecordRepository.findByMaterialAndWarehouse(materialId, warehouseId, pageable);
        return records.getContent().stream()
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        BeanUtils.copyProperties(inventory, dto);
        if (inventory.getMaterial() != null) {
            dto.setMaterialId(inventory.getMaterial().getId());
            dto.setMaterialCode(inventory.getMaterial().getMaterialCode());
            dto.setMaterialName(inventory.getMaterial().getMaterialName());
            dto.setSpecification(inventory.getMaterial().getSpecification());
        }
        if (inventory.getWarehouse() != null) {
            dto.setWarehouseId(inventory.getWarehouse().getId());
            dto.setWarehouseName(inventory.getWarehouse().getWarehouseName());
        }
        if (inventory.getLocation() != null) {
            dto.setLocationId(inventory.getLocation().getId());
            dto.setLocationCode(inventory.getLocation().getLocationCode());
            dto.setLocationName(inventory.getLocation().getLocationName());
        }
        return dto;
    }

    private InventoryRecordDTO convertRecordToDTO(InventoryRecord record) {
        InventoryRecordDTO dto = new InventoryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        if (record.getMaterial() != null) {
            dto.setMaterialId(record.getMaterial().getId());
            dto.setMaterialCode(record.getMaterial().getMaterialCode());
            dto.setMaterialName(record.getMaterial().getMaterialName());
        }
        if (record.getWarehouse() != null) {
            dto.setWarehouseId(record.getWarehouse().getId());
            dto.setWarehouseName(record.getWarehouse().getWarehouseName());
        }
        if (record.getLocation() != null) {
            dto.setLocationId(record.getLocation().getId());
            dto.setLocationCode(record.getLocation().getLocationCode());
        }
        if (record.getOperator() != null) {
            dto.setOperatorId(record.getOperator().getId());
            dto.setOperatorName(record.getOperator().getRealName());
        }
        return dto;
    }
}
