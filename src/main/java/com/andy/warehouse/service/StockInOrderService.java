package com.andy.warehouse.service;

import com.andy.warehouse.dto.StockInOrderCreateRequest;
import com.andy.warehouse.entity.StockInOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StockInOrderService {

    StockInOrder create(StockInOrderCreateRequest request);

    void approve(Long id);

    void reject(Long id);

    void delete(Long id);

    StockInOrder getById(Long id);

    List<StockInOrder> getByWarehouseId(Long warehouseId);

    Page<StockInOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword);
}
