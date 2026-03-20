package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.StockOutOrderCreateRequest;
import com.andy.warehouse.dto.StockOutOrderItemRequest;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.repository.MaterialRepository;
import com.andy.warehouse.repository.StockOutOrderRepository;
import com.andy.warehouse.repository.WarehouseRepository;
import com.andy.warehouse.security.SecurityUser;
import com.andy.warehouse.service.StockOutOrderService;
import com.andy.warehouse.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockOutOrderServiceImpl implements StockOutOrderService {

    private final StockOutOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final StockService stockService;

    @Override
    @Transactional
    public StockOutOrder create(StockOutOrderCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        StockOutOrder order = new StockOutOrder();
        order.setOrderNo(generateOrderNo("CK"));
        order.setOrderType(request.getOrderType());
        order.setWarehouse(warehouse);
        order.setReceiver(request.getReceiver());
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        order.setStatus(0);
        order.setRemark(request.getRemark());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockOutOrderItemRequest itemRequest : request.getItems()) {
            Material material = materialRepository.findById(itemRequest.getMaterialId())
                    .orElseThrow(() -> new BusinessException("物资不存在: " + itemRequest.getMaterialId()));
            StockOutOrderItem item = new StockOutOrderItem();
            item.setOrder(order);
            item.setMaterial(material);
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
        return orderRepository.save(order);
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
                    order.getWarehouse().getId(),
                    item.getMaterial().getId(),
                    item.getQuantity()
            );
        }
        order.setStatus(1);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void reject(Long id) {
        StockOutOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能驳回");
        }
        order.setStatus(2);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StockOutOrder order = getById(id);
        if (order.getStatus() == 1) {
            throw new BusinessException("已审核的订单不能删除");
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public StockOutOrder getById(Long id) {
        return orderRepository.findById(id)
                .filter(o -> !o.getDeleted())
                .orElseThrow(() -> new BusinessException("出库单不存在"));
    }

    @Override
    public List<StockOutOrder> getByWarehouseId(Long warehouseId) {
        return orderRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public Page<StockOutOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<StockOutOrder> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            if (warehouseId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("warehouse").get("id"), warehouseId));
            }
            if (StringUtils.hasText(keyword)) {
                predicates = cb.and(predicates, cb.like(root.get("orderNo"), "%" + keyword + "%"));
            }
            return predicates;
        };
        return orderRepository.findAll(spec, pageable);
    }
}
