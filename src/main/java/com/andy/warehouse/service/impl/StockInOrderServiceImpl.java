package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.StockInOrderCreateRequest;
import com.andy.warehouse.dto.StockInOrderItemRequest;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.StockInOrder;
import com.andy.warehouse.entity.StockInOrderItem;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.mapper.StockInOrderMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.security.SecurityUser;
import com.andy.warehouse.service.StockInOrderService;
import com.andy.warehouse.service.StockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockInOrderServiceImpl implements StockInOrderService {

    private final StockInOrderMapper orderMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;
    private final StockService stockService;

    @Override
    @Transactional
    public StockInOrder create(StockInOrderCreateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        StockInOrder order = new StockInOrder();
        order.setOrderNo(generateOrderNo("RK"));
        order.setOrderType(request.getOrderType());
        order.setWarehouseId(warehouse.getId());
        order.setSupplier(request.getSupplier());
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        order.setStatus(0);
        order.setRemark(request.getRemark());
        order.setItems(new ArrayList<>());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockInOrderItemRequest itemRequest : request.getItems()) {
            Material material = materialMapper.selectById(itemRequest.getMaterialId());
            if (material == null) {
                throw new BusinessException("物资不存在: " + itemRequest.getMaterialId());
            }
            StockInOrderItem item = new StockInOrderItem();
            item.setOrderId(order.getId());
            item.setMaterialId(material.getId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : material.getPrice());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setBatchNo(itemRequest.getBatchNo());
            item.setPosition(itemRequest.getPosition());
            item.setRemark(itemRequest.getRemark());
            order.getItems().add(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        order.setTotalAmount(totalAmount);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser securityUser) {
            order.setOperatorId(securityUser.getId());
            order.setOperatorName(securityUser.getUsername());
        }
        orderMapper.insert(order);
        return order;
    }

    private String generateOrderNo(String prefix) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + dateStr + random;
    }

    @Override
    @Transactional
    public void approve(Long id) {
        StockInOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能审核");
        }
        for (StockInOrderItem item : order.getItems()) {
            stockService.addStock(
                    order.getWarehouseId(),
                    item.getMaterialId(),
                    item.getQuantity(),
                    item.getBatchNo(),
                    item.getPosition()
            );
        }
        order.setStatus(1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void reject(Long id) {
        StockInOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能驳回");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StockInOrder order = getById(id);
        if (order.getStatus() == 1) {
            throw new BusinessException("已审核的订单不能删除");
        }
        order.setDeleted(true);
        orderMapper.updateById(order);
    }

    @Override
    public StockInOrder getById(Long id) {
        StockInOrder order = orderMapper.selectById(id);
        if (order == null || order.getDeleted()) {
            throw new BusinessException("入库单不存在");
        }
        loadOrderRelations(order);
        return order;
    }

    private void loadOrderRelations(StockInOrder order) {
        if (order.getWarehouseId() != null) {
            Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
            order.setWarehouse(warehouse);
        }
        List<StockInOrderItem> items = orderMapper.findItemsByOrderId(order.getId());
        order.setItems(items != null ? items : new ArrayList<>());
        for (StockInOrderItem item : order.getItems()) {
            if (item.getMaterialId() != null) {
                Material material = materialMapper.selectById(item.getMaterialId());
                item.setMaterial(material);
            }
        }
    }

    @Override
    public List<StockInOrder> getByWarehouseId(Long warehouseId) {
        List<StockInOrder> orders = orderMapper.findByWarehouseId(warehouseId);
        for (StockInOrder order : orders) {
            loadOrderRelations(order);
        }
        return orders;
    }

    @Override
    public Page<StockInOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword) {
        Page<StockInOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StockInOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInOrder::getDeleted, false);
        if (warehouseId != null) {
            wrapper.eq(StockInOrder::getWarehouseId, warehouseId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StockInOrder::getOrderNo, keyword);
        }
        wrapper.orderByDesc(StockInOrder::getCreatedAt);
        IPage<StockInOrder> orderPage = orderMapper.selectPage(page, wrapper);
        for (StockInOrder order : orderPage.getRecords()) {
            loadOrderRelations(order);
        }
        return (Page<StockInOrder>) orderPage;
    }
}
