package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.*;
import com.andy.warehouse.service.StockOutService;
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
public class StockOutServiceImpl implements StockOutService {

    private final StockOutOrderMapper stockOutOrderMapper;
    private final StockOutItemMapper stockOutItemMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;
    private final WarehouseLocationMapper locationMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final UserMapper userMapper;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public StockOutOrderDTO createStockOut(StockOutCreateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            throw new ResourceNotFoundException("仓库不存在");
        }

        StockOutOrder order = new StockOutOrder();
        order.setOrderNo(generateOrderNo());
        order.setOrderType(request.getOrderType());
        order.setWarehouseId(request.getWarehouseId());
        order.setRecipientName(request.getRecipientName());
        order.setRecipientDept(request.getRecipientDept());
        order.setRecipientContact(request.getRecipientContact());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setOrderDate(java.time.LocalDate.now());
        order.setExpectedDate(request.getExpectedDate());
        order.setStatus("PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        List<StockOutItem> items = new ArrayList<>();

        for (StockOutCreateRequest.StockOutItemRequest itemRequest : request.getItems()) {
            Material material = materialMapper.selectById(itemRequest.getMaterialId());
            if (material == null) {
                throw new ResourceNotFoundException("物资不存在");
            }

            checkStock(request.getWarehouseId(), itemRequest.getMaterialId(), itemRequest.getQuantity());

            StockOutItem item = new StockOutItem();
            item.setMaterialId(itemRequest.getMaterialId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit() != null ? itemRequest.getUnit() : material.getUnit());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setBatchNo(itemRequest.getBatchNo());
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

        stockOutOrderMapper.insert(order);

        for (StockOutItem item : items) {
            item.setStockOutOrderId(order.getId());
            stockOutItemMapper.insert(item);
        }

        return convertToDTO(order, items);
    }

    private void checkStock(Long warehouseId, Long materialId, BigDecimal quantity) {
        BigDecimal totalStock = inventoryMapper.getTotalQuantityByMaterialId(materialId);
        if (totalStock == null || totalStock.compareTo(quantity) < 0) {
            throw new BusinessException("库存不足");
        }
    }

    @Override
    @Transactional
    public StockOutOrderDTO confirmStockOut(Long id) {
        StockOutOrder order = stockOutOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("出库单不存在");
        }

        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("只有已审批的出库单才能确认出库");
        }

        order.setStatus("COMPLETED");
        order.setActualDate(LocalDateTime.now());
        stockOutOrderMapper.updateById(order);

        List<StockOutItem> items = stockOutItemMapper.findByStockOutOrderId(id);
        for (StockOutItem item : items) {
            item.setStatus("COMPLETED");
            item.setActualQuantity(item.getQuantity());
            stockOutItemMapper.updateById(item);

            deductInventory(order.getWarehouseId(), item);
            createInventoryRecord(order, item, "OUT");
        }

        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public StockOutOrderDTO approveStockOut(Long id) {
        StockOutOrder order = stockOutOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("出库单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("只有待审批的出库单才能审批");
        }

        order.setStatus("APPROVED");
        order.setApproveTime(LocalDateTime.now());
        stockOutOrderMapper.updateById(order);

        List<StockOutItem> items = stockOutItemMapper.findByStockOutOrderId(id);
        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public StockOutOrderDTO cancelStockOut(Long id) {
        StockOutOrder order = stockOutOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("出库单不存在");
        }

        if ("COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("已完成的出库单不能取消");
        }

        order.setStatus("CANCELLED");
        stockOutOrderMapper.updateById(order);

        List<StockOutItem> items = stockOutItemMapper.findByStockOutOrderId(id);
        for (StockOutItem item : items) {
            item.setStatus("CANCELLED");
            stockOutItemMapper.updateById(item);
        }

        return convertToDTO(order, items);
    }

    @Override
    public StockOutOrderDTO getStockOutById(Long id) {
        StockOutOrder order = stockOutOrderMapper.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("出库单不存在");
        }
        List<StockOutItem> items = stockOutItemMapper.findByStockOutOrderId(id);
        return convertToDTO(order, items);
    }

    @Override
    public StockOutOrderDTO getStockOutByOrderNo(String orderNo) {
        StockOutOrder order = stockOutOrderMapper.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));
        List<StockOutItem> items = stockOutItemMapper.findByStockOutOrderId(order.getId());
        return convertToDTO(order, items);
    }

    @Override
    public PageResult<StockOutOrderDTO> getStockOutList(StockQueryRequest request) {
        Page<StockOutOrder> page = new Page<>(request.getPage(), request.getSize());
        IPage<StockOutOrder> orderPage = stockOutOrderMapper.findByConditions(
                page,
                request.getOrderNo(),
                request.getOrderType(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );
        List<StockOutOrderDTO> dtoList = orderPage.getRecords().stream()
                .map(order -> convertToDTO(order, stockOutItemMapper.findByStockOutOrderId(order.getId())))
                .collect(Collectors.toList());
        return PageResult.of(dtoList, orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize());
    }

    private void deductInventory(Long warehouseId, StockOutItem item) {
        List<Inventory> inventories = inventoryMapper.findByMaterialIdAndWarehouseId(
                item.getMaterialId(), warehouseId);

        BigDecimal remainingQty = item.getQuantity();

        for (Inventory inventory : inventories) {
            if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;

            if (inventory.getAvailableQuantity().compareTo(remainingQty) >= 0) {
                inventory.setQuantity(inventory.getQuantity().subtract(remainingQty));
                inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(remainingQty));
                remainingQty = BigDecimal.ZERO;
            } else {
                remainingQty = remainingQty.subtract(inventory.getAvailableQuantity());
                inventory.setQuantity(inventory.getQuantity().subtract(inventory.getAvailableQuantity()));
                inventory.setAvailableQuantity(BigDecimal.ZERO);
            }

            inventoryMapper.updateById(inventory);
        }
    }

    private void createInventoryRecord(StockOutOrder order, StockOutItem item, String recordType) {
        InventoryRecord record = new InventoryRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(recordType);
        record.setBizType(order.getOrderType());
        record.setBizNo(order.getOrderNo());
        record.setMaterialId(item.getMaterialId());
        record.setWarehouseId(order.getWarehouseId());
        record.setLocationId(item.getLocationId());
        record.setQuantity(item.getQuantity().negate());
        record.setUnit(item.getUnit());
        record.setBatchNo(item.getBatchNo());
        record.setRemark(item.getRemark());
        inventoryRecordMapper.insert(record);
    }

    private String generateOrderNo() {
        return "OUT" + idGenerator.nextId();
    }

    private String generateRecordNo() {
        return "REC" + idGenerator.nextId();
    }

    private StockOutOrderDTO convertToDTO(StockOutOrder order, List<StockOutItem> items) {
        StockOutOrderDTO dto = new StockOutOrderDTO();
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

    private StockOutItemDTO convertItemToDTO(StockOutItem item) {
        StockOutItemDTO dto = new StockOutItemDTO();
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
