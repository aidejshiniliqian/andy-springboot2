package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.StockOutOrderCreateRequest;
import com.andy.warehouse.dto.StockOutOrderItemRequest;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.StockOutOrder;
import com.andy.warehouse.entity.StockOutOrderItem;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.mapper.StockOutOrderMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.security.SecurityUser;
import com.andy.warehouse.service.StockOutOrderService;
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
public class StockOutOrderServiceImpl implements StockOutOrderService {

    private final StockOutOrderMapper orderMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;
    private final StockService stockService;

    @Override
    @Transactional
    public StockOutOrder create(StockOutOrderCreateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        StockOutOrder order = new StockOutOrder();
        order.setOrderNo(generateOrderNo("CK"));
        order.setOrderType(request.getOrderType());
        order.setWarehouseId(warehouse.getId());
        order.setReceiver(request.getReceiver());
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        order.setStatus(0);
        order.setRemark(request.getRemark());
        order.setItems(new ArrayList<>());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockOutOrderItemRequest itemRequest : request.getItems()) {
            Material material = materialMapper.selectById(itemRequest.getMaterialId());
            if (material == null) {
                throw new BusinessException("物资不存在: " + itemRequest.getMaterialId());
            }
            StockOutOrderItem item = new StockOutOrderItem();
            item.setOrderId(order.getId());
            item.setMaterialId(material.getId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : material.getPrice());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setBatchNo(itemRequest.getBatchNo());
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
        StockOutOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能审核");
        }
        for (StockOutOrderItem item : order.getItems()) {
            stockService.subtractStock(
                    order.getWarehouseId(),
                    item.getMaterialId(),
                    item.getQuantity()
            );
        }
        order.setStatus(1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void reject(Long id) {
        StockOutOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能驳回");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StockOutOrder order = getById(id);
        if (order.getStatus() == 1) {
            throw new BusinessException("已审核的订单不能删除");
        }
        order.setDeleted(true);
        orderMapper.updateById(order);
    }

    @Override
    public StockOutOrder getById(Long id) {
        StockOutOrder order = orderMapper.selectById(id);
        if (order == null || order.getDeleted()) {
            throw new BusinessException("出库单不存在");
        }
        loadOrderRelations(order);
        return order;
    }

    private void loadOrderRelations(StockOutOrder order) {
        if (order.getWarehouseId() != null) {
            Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
            order.setWarehouse(warehouse);
        }
        List<StockOutOrderItem> items = orderMapper.findItemsByOrderId(order.getId());
        order.setItems(items != null ? items : new ArrayList<>());
        for (StockOutOrderItem item : order.getItems()) {
            if (item.getMaterialId() != null) {
                Material material = materialMapper.selectById(item.getMaterialId());
                item.setMaterial(material);
            }
        }
    }

    @Override
    public List<StockOutOrder> getByWarehouseId(Long warehouseId) {
        List<StockOutOrder> orders = orderMapper.findByWarehouseId(warehouseId);
        for (StockOutOrder order : orders) {
            loadOrderRelations(order);
        }
        return orders;
    }

    @Override
    public Page<StockOutOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword) {
        Page<StockOutOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StockOutOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockOutOrder::getDeleted, false);
        if (warehouseId != null) {
            wrapper.eq(StockOutOrder::getWarehouseId, warehouseId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StockOutOrder::getOrderNo, keyword);
        }
        wrapper.orderByDesc(StockOutOrder::getCreatedAt);
        IPage<StockOutOrder> orderPage = orderMapper.selectPage(page, wrapper);
        for (StockOutOrder order : orderPage.getRecords()) {
            loadOrderRelations(order);
        }
        return (Page<StockOutOrder>) orderPage;
    }
}
