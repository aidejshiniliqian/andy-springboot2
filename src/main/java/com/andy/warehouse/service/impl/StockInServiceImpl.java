package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.*;
import com.andy.warehouse.service.StockInService;
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
public class StockInServiceImpl implements StockInService {

    private final StockInOrderRepository stockInOrderRepository;
    private final StockInItemRepository stockInItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseLocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public StockInOrderDTO createStockIn(StockInCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("仓库不存在"));

        StockInOrder order = new StockInOrder();
        order.setOrderNo(generateOrderNo());
        order.setOrderType(request.getOrderType());
        order.setWarehouse(warehouse);
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
            Material material = materialRepository.findById(itemRequest.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("物资不存在"));

            StockInItem item = new StockInItem();
            item.setMaterial(material);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit() != null ? itemRequest.getUnit() : material.getUnit());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setBatchNo(itemRequest.getBatchNo());
            item.setProductionDate(itemRequest.getProductionDate());
            item.setExpiryDate(itemRequest.getExpiryDate());
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

        StockInOrder savedOrder = stockInOrderRepository.save(order);

        for (StockInItem item : items) {
            item.setStockInOrder(savedOrder);
        }
        stockInItemRepository.saveAll(items);

        savedOrder.setItems(items);
        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional
    public StockInOrderDTO confirmStockIn(Long id) {
        StockInOrder order = stockInOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("入库单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("只有待确认的入库单才能确认");
        }

        order.setStatus("COMPLETED");
        order.setActualDate(LocalDateTime.now());

        for (StockInItem item : order.getItems()) {
            item.setStatus("COMPLETED");
            item.setActualQuantity(item.getQuantity());
            stockInItemRepository.save(item);

            updateInventory(order.getWarehouse(), item);
            createInventoryRecord(order, item, "IN");
        }

        StockInOrder updatedOrder = stockInOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public StockInOrderDTO cancelStockIn(Long id) {
        StockInOrder order = stockInOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("入库单不存在"));

        if ("COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("已完成的入库单不能取消");
        }

        order.setStatus("CANCELLED");

        for (StockInItem item : order.getItems()) {
            item.setStatus("CANCELLED");
            stockInItemRepository.save(item);
        }

        StockInOrder updatedOrder = stockInOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    public StockInOrderDTO getStockInById(Long id) {
        StockInOrder order = stockInOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("入库单不存在"));
        return convertToDTO(order);
    }

    @Override
    public StockInOrderDTO getStockInByOrderNo(String orderNo) {
        StockInOrder order = stockInOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("入库单不存在"));
        return convertToDTO(order);
    }

    @Override
    public PageResult<StockInOrderDTO> getStockInList(StockQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<StockInOrder> orderPage = stockInOrderRepository.findByConditions(
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

    private void updateInventory(Warehouse warehouse, StockInItem item) {
        Inventory inventory = inventoryRepository
                .findByMaterialIdAndWarehouseIdAndLocationIdAndBatchNo(
                        item.getMaterial().getId(),
                        warehouse.getId(),
                        item.getLocation() != null ? item.getLocation().getId() : null,
                        item.getBatchNo()
                ).orElse(null);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setMaterial(item.getMaterial());
            inventory.setWarehouse(warehouse);
            inventory.setLocation(item.getLocation());
            inventory.setQuantity(item.getQuantity());
            inventory.setAvailableQuantity(item.getQuantity());
            inventory.setLockedQuantity(BigDecimal.ZERO);
            inventory.setUnit(item.getUnit());
            inventory.setBatchNo(item.getBatchNo());
            inventory.setProductionDate(item.getProductionDate());
            inventory.setExpiryDate(item.getExpiryDate());
            inventory.setStatus(1);
        } else {
            inventory.setQuantity(inventory.getQuantity().add(item.getQuantity()));
            inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(item.getQuantity()));
        }

        inventoryRepository.save(inventory);
    }

    private void createInventoryRecord(StockInOrder order, StockInItem item, String recordType) {
        InventoryRecord record = new InventoryRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(recordType);
        record.setBizType(order.getOrderType());
        record.setBizNo(order.getOrderNo());
        record.setMaterial(item.getMaterial());
        record.setWarehouse(order.getWarehouse());
        record.setLocation(item.getLocation());
        record.setQuantity(item.getQuantity());
        record.setUnit(item.getUnit());
        record.setBatchNo(item.getBatchNo());
        record.setRemark(item.getRemark());
        inventoryRecordRepository.save(record);
    }

    private String generateOrderNo() {
        return "IN" + idGenerator.nextId();
    }

    private String generateRecordNo() {
        return "REC" + idGenerator.nextId();
    }

    private StockInOrderDTO convertToDTO(StockInOrder order) {
        StockInOrderDTO dto = new StockInOrderDTO();
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

    private StockInItemDTO convertItemToDTO(StockInItem item) {
        StockInItemDTO dto = new StockInItemDTO();
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
