package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.*;
import com.andy.warehouse.service.StockInService;
import com.andy.warehouse.util.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockInServiceImpl implements StockInService {

    private final StockInOrderMapper stockInOrderMapper;
    private final StockInItemMapper stockInItemMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;
    private final WarehouseLocationMapper locationMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final UserMapper userMapper;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public StockInOrderDTO createStockIn(StockInCreateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }

        StockInOrder order = new StockInOrder();
        order.setOrderNo(generateOrderNo());
        order.setOrderType(request.getOrderType());
        order.setWarehouseId(request.getWarehouseId());
        order.setSupplierName(request.getSupplierName());
        order.setSupplierContact(request.getSupplierContact());
        order.setSupplierPhone(request.getSupplierPhone());
        order.setOrderDate(java.time.LocalDate.now());
        order.setExpectedDate(request.getExpectedDate());
        order.setStatus("PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        List<StockInItem> items = new ArrayList<>();

        for (StockInCreateRequest.StockInItemRequest itemRequest : request.getItems()) {
            Material material = materialMapper.selectById(itemRequest.getMaterialId());
            if (material == null) {
                throw new ResourceNotFoundException("物资不存在");
            }

            StockInItem item = new StockInItem();
            item.setMaterialId(itemRequest.getMaterialId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit() != null ? itemRequest.getUnit() : material.getUnit());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setBatchNo(itemRequest.getBatchNo());
            item.setProductionDate(itemRequest.getProductionDate());
            item.setExpiryDate(itemRequest.getExpiryDate());
            item.setRemark(itemRequest.getRemark());
            item.setStatus("PENDING");

            if (itemRequest.getLocationId() != null) {
                WarehouseLocation location = locationMapper.selectById(itemRequest.getLocationId());
                if (location == null) {
                    throw new ResourceNotFoundException("库位不存在");
                }
                item.setLocationId(itemRequest.getLocationId());
            }

            if (itemRequest.getUnitPrice() != null) {
                item.setTotalAmount(itemRequest.getUnitPrice().multiply(itemRequest.getQuantity()));
                totalAmount = totalAmount.add(item.getTotalAmount());
            }
            totalQuantity = totalQuantity.add(itemRequest.getQuantity());

            items.add(item);
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);

        stockInOrderMapper.insert(order);

        for (StockInItem item : items) {
            item.setStockInOrderId(order.getId());
            stockInItemMapper.insert(item);
        }

        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public StockInOrderDTO confirmStockIn(Long id) {
        StockInOrder order = stockInOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("入库单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("只有待确认的入库单才能确认");
        }

        order.setStatus("COMPLETED");
        order.setActualDate(LocalDateTime.now());
        stockInOrderMapper.updateById(order);

        List<StockInItem> items = stockInItemMapper.findByStockInOrderId(id);
        for (StockInItem item : items) {
            item.setStatus("COMPLETED");
            item.setActualQuantity(item.getQuantity());
            stockInItemMapper.updateById(item);

            updateInventory(order.getWarehouseId(), item);
            createInventoryRecord(order, item, "IN");
        }

        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public StockInOrderDTO cancelStockIn(Long id) {
        StockInOrder order = stockInOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("入库单不存在");
        }

        if ("COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("已完成的入库单不能取消");
        }

        order.setStatus("CANCELLED");
        stockInOrderMapper.updateById(order);

        List<StockInItem> items = stockInItemMapper.findByStockInOrderId(id);
        for (StockInItem item : items) {
            item.setStatus("CANCELLED");
            stockInItemMapper.updateById(item);
        }

        return convertToDTO(order, items);
    }

    @Override
    public StockInOrderDTO getStockInById(Long id) {
        StockInOrder order = stockInOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("入库单不存在");
        }
        List<StockInItem> items = stockInItemMapper.findByStockInOrderId(id);
        return convertToDTO(order, items);
    }

    @Override
    public StockInOrderDTO getStockInByOrderNo(String orderNo) {
        StockInOrder order = stockInOrderMapper.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("入库单不存在"));
        List<StockInItem> items = stockInItemMapper.findByStockInOrderId(order.getId());
        return convertToDTO(order, items);
    }

    @Override
    public PageResult<StockInOrderDTO> getStockInList(StockQueryRequest request) {
        Page<StockInOrder> page = new Page<>(request.getPage(), request.getSize());
        IPage<StockInOrder> orderPage = stockInOrderMapper.findByConditions(
                page,
                request.getOrderNo(),
                request.getOrderType(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );
        List<StockInOrderDTO> dtoList = orderPage.getRecords().stream()
                .map(order -> convertToDTO(order, stockInItemMapper.findByStockInOrderId(order.getId())))
                .collect(Collectors.toList());
        return PageResult.of(dtoList, orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize());
    }

    private void updateInventory(Long warehouseId, StockInItem item) {
        Inventory inventory = inventoryMapper
                .findByMaterialIdAndWarehouseIdAndLocationIdAndBatchNo(
                        item.getMaterialId(),
                        warehouseId,
                        item.getLocationId(),
                        item.getBatchNo()
                ).orElse(null);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setMaterialId(item.getMaterialId());
            inventory.setWarehouseId(warehouseId);
            inventory.setLocationId(item.getLocationId());
            inventory.setQuantity(item.getQuantity());
            inventory.setAvailableQuantity(item.getQuantity());
            inventory.setLockedQuantity(BigDecimal.ZERO);
            inventory.setUnit(item.getUnit());
            inventory.setBatchNo(item.getBatchNo());
            inventory.setProductionDate(item.getProductionDate());
            inventory.setExpiryDate(item.getExpiryDate());
            inventory.setStatus(1);
            inventoryMapper.insert(inventory);
        } else {
            inventory.setQuantity(inventory.getQuantity().add(item.getQuantity()));
            inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(item.getQuantity()));
            inventoryMapper.updateById(inventory);
        }
    }

    private void createInventoryRecord(StockInOrder order, StockInItem item, String recordType) {
        InventoryRecord record = new InventoryRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(recordType);
        record.setBizType(order.getOrderType());
        record.setBizNo(order.getOrderNo());
        record.setMaterialId(item.getMaterialId());
        record.setWarehouseId(order.getWarehouseId());
        record.setLocationId(item.getLocationId());
        record.setQuantity(item.getQuantity());
        record.setUnit(item.getUnit());
        record.setBatchNo(item.getBatchNo());
        record.setRemark(item.getRemark());
        inventoryRecordMapper.insert(record);
    }

    private String generateOrderNo() {
        return "IN" + idGenerator.nextId();
    }

    private String generateRecordNo() {
        return "REC" + idGenerator.nextId();
    }

    private StockInOrderDTO convertToDTO(StockInOrder order, List<StockInItem> items) {
        StockInOrderDTO dto = new StockInOrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setWarehouseId(order.getWarehouseId());
        Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        if (warehouse != null) {
            dto.setWarehouseName(warehouse.getWarehouseName());
        }
        if (order.getOperatorId() != null) {
            dto.setOperatorId(order.getOperatorId());
            User operator = userMapper.selectById(order.getOperatorId());
            if (operator != null) {
                dto.setOperatorName(operator.getRealName());
            }
        }
        if (items != null) {
            dto.setItems(items.stream().map(this::convertItemToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private StockInItemDTO convertItemToDTO(StockInItem item) {
        StockInItemDTO dto = new StockInItemDTO();
        BeanUtils.copyProperties(item, dto);
        dto.setMaterialId(item.getMaterialId());
        Material material = materialMapper.selectById(item.getMaterialId());
        if (material != null) {
            dto.setMaterialCode(material.getMaterialCode());
            dto.setMaterialName(material.getMaterialName());
            dto.setSpecification(material.getSpecification());
        }
        dto.setUnit(item.getUnit());
        if (item.getLocationId() != null) {
            dto.setLocationId(item.getLocationId());
            WarehouseLocation location = locationMapper.selectById(item.getLocationId());
            if (location != null) {
                dto.setLocationCode(location.getLocationCode());
                dto.setLocationName(location.getLocationName());
            }
        }
        return dto;
    }
}
