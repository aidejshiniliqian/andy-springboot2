package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.StockInOrderCreateRequest;
import com.andy.warehouse.dto.StockInOrderItemRequest;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.repository.MaterialRepository;
import com.andy.warehouse.repository.StockInOrderRepository;
import com.andy.warehouse.repository.WarehouseRepository;
import com.andy.warehouse.security.SecurityUser;
import com.andy.warehouse.service.StockInOrderService;
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
public class StockInOrderServiceImpl implements StockInOrderService {

    private final StockInOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final StockService stockService;

    @Override
    @Transactional
    public StockInOrder create(StockInOrderCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        StockInOrder order = new StockInOrder();
        order.setOrderNo(generateOrderNo("RK"));
        order.setOrderType(request.getOrderType());
        order.setWarehouse(warehouse);
        order.setSupplier(request.getSupplier());
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        order.setStatus(0);
        order.setRemark(request.getRemark());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockInOrderItemRequest itemRequest : request.getItems()) {
            Material material = materialRepository.findById(itemRequest.getMaterialId())
                    .orElseThrow(() -> new BusinessException("物资不存在: " + itemRequest.getMaterialId()));
            StockInOrderItem item = new StockInOrderItem();
            item.setOrder(order);
            item.setMaterial(material);
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
        StockInOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能审核");
        }
        for (StockInOrderItem item : order.getItems()) {
            stockService.addStock(
                    order.getWarehouse().getId(),
                    item.getMaterial().getId(),
                    item.getQuantity(),
                    item.getBatchNo(),
                    item.getPosition()
            );
        }
        order.setStatus(1);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void reject(Long id) {
        StockInOrder order = getById(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的订单才能驳回");
        }
        order.setStatus(2);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StockInOrder order = getById(id);
        if (order.getStatus() == 1) {
            throw new BusinessException("已审核的订单不能删除");
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public StockInOrder getById(Long id) {
        return orderRepository.findById(id)
                .filter(o -> !o.getDeleted())
                .orElseThrow(() -> new BusinessException("入库单不存在"));
    }

    @Override
    public List<StockInOrder> getByWarehouseId(Long warehouseId) {
        return orderRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public Page<StockInOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<StockInOrder> spec = (root, query, cb) -> {
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
