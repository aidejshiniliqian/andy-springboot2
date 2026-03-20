package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.*;
import com.andy.warehouse.service.StockOutService;
import com.andy.warehouse.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final StockOutOrderRepository stockOutOrderRepository;
    private final StockOutItemRepository stockOutItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseLocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public StockOutOrderDTO createStockOut(StockOutCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));

        StockOutOrder order = new StockOutOrder();
        order.setOrderNo(generateOrderNo());
        order.setOrderType(request.getOrderType());
        order.setWarehouse(warehouse);
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
            Material material = materialRepository.findById(itemRequest.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));

            checkStock(warehouse.getId(), material.getId(), itemRequest.getQuantity());

            StockOutItem item = new StockOutItem();
            item.setMaterial(material);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit() != null ? itemRequest.getUnit() : material.getUnit());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setBatchNo(itemRequest.getBatchNo());
            item.setRemark(itemRequest.getRemark());
            item.setStatus("PENDING");

            if (itemRequest.getLocationId() != null) {
                WarehouseLocation location = locationRepository.findById(itemRequest.getLocationId())
                        .orElseThrow(() -> new ResourceNotFoundException("库位不存在"));
                item.setLocation(location);
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

        StockOutOrder savedOrder = stockOutOrderRepository.save(order);

        for (StockOutItem item : items) {
            item.setStockOutOrder(savedOrder);
        }
        stockOutItemRepository.saveAll(items);

        savedOrder.setItems(items);
        return convertToDTO(savedOrder);
    }

    private void checkStock(Long warehouseId, Long materialId, BigDecimal quantity) {
        BigDecimal totalStock = inventoryRepository.getTotalQuantityByMaterialId(materialId);
        if (totalStock == null || totalStock.compareTo(quantity) < 0) {
            throw new BusinessException("库存不足");
        }
    }

    @Override
    @Transactional
    public StockOutOrderDTO confirmStockOut(Long id) {
        StockOutOrder order = stockOutOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));

        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("只有已审批的出库单才能确认出库");
        }

        order.setStatus("COMPLETED");
        order.setActualDate(LocalDateTime.now());

        for (StockOutItem item : order.getItems()) {
            item.setStatus("COMPLETED");
            item.setActualQuantity(item.getQuantity());
            stockOutItemRepository.save(item);

            deductInventory(order.getWarehouse(), item);
            createInventoryRecord(order, item, "OUT");
        }

        StockOutOrder updatedOrder = stockOutOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public StockOutOrderDTO approveStockOut(Long id) {
        StockOutOrder order = stockOutOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("只有待审批的出库单才能审批");
        }

        order.setStatus("APPROVED");
        order.setApproveTime(LocalDateTime.now());

        StockOutOrder updatedOrder = stockOutOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public StockOutOrderDTO cancelStockOut(Long id) {
        StockOutOrder order = stockOutOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));

        if ("COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("已完成的出库单不能取消");
        }

        order.setStatus("CANCELLED");

        for (StockOutItem item : order.getItems()) {
            item.setStatus("CANCELLED");
            stockOutItemRepository.save(item);
        }

        StockOutOrder updatedOrder = stockOutOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    public StockOutOrderDTO getStockOutById(Long id) {
        StockOutOrder order = stockOutOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));
        return convertToDTO(order);
    }

    @Override
    public StockOutOrderDTO getStockOutByOrderNo(String orderNo) {
        StockOutOrder order = stockOutOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("出库单不存在"));
        return convertToDTO(order);
    }

    @Override
    public PageResult<StockOutOrderDTO> getStockOutList(StockQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<StockOutOrder> orderPage = stockOutOrderRepository.findByConditions(
                request.getOrderNo(),
                request.getOrderType(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );
        return PageResult.of(orderPage.map(this::convertToDTO));
    }

    private void deductInventory(Warehouse warehouse, StockOutItem item) {
        List<Inventory> inventories = inventoryRepository.findByMaterialIdAndWarehouseId(
                item.getMaterial().getId(), warehouse.getId());

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

            inventoryRepository.save(inventory);
        }
    }

    private void createInventoryRecord(StockOutOrder order, StockOutItem item, String recordType) {
        InventoryRecord record = new InventoryRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(recordType);
        record.setBizType(order.getOrderType());
        record.setBizNo(order.getOrderNo());
        record.setMaterial(item.getMaterial());
        record.setWarehouse(order.getWarehouse());
        record.setLocation(item.getLocation());
        record.setQuantity(item.getQuantity().negate());
        record.setUnit(item.getUnit());
        record.setBatchNo(item.getBatchNo());
        record.setRemark(item.getRemark());
        inventoryRecordRepository.save(record);
    }

    private String generateOrderNo() {
        return "OUT" + idGenerator.nextId();
    }

    private String generateRecordNo() {
        return "REC" + idGenerator.nextId();
    }

    private StockOutOrderDTO convertToDTO(StockOutOrder order) {
        StockOutOrderDTO dto = new StockOutOrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setWarehouseId(order.getWarehouse().getId());
        dto.setWarehouseName(order.getWarehouse().getWarehouseName());
        if (order.getOperator() != null) {
            dto.setOperatorId(order.getOperator().getId());
            dto.setOperatorName(order.getOperator().getRealName());
        }
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private StockOutItemDTO convertItemToDTO(StockOutItem item) {
        StockOutItemDTO dto = new StockOutItemDTO();
        BeanUtils.copyProperties(item, dto);
        dto.setMaterialId(item.getMaterial().getId());
        dto.setMaterialCode(item.getMaterial().getMaterialCode());
        dto.setMaterialName(item.getMaterial().getMaterialName());
        dto.setSpecification(item.getMaterial().getSpecification());
        dto.setUnit(item.getUnit());
        if (item.getLocation() != null) {
            dto.setLocationId(item.getLocation().getId());
            dto.setLocationCode(item.getLocation().getLocationCode());
            dto.setLocationName(item.getLocation().getLocationName());
        }
        return dto;
    }
}
