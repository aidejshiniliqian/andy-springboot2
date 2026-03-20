package com.andy.warehouse.service;

import com.andy.warehouse.dto.StockOutOrderCreateRequest;
import com.andy.warehouse.entity.StockOutOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StockOutOrderService {

    StockOutOrder create(StockOutOrderCreateRequest request);

    void approve(Long id);

    void reject(Long id);

    void delete(Long id);

    StockOutOrder getById(Long id);

    List<StockOutOrder> getByWarehouseId(Long warehouseId);

    Page<StockOutOrder> getPage(Long warehouseId, Integer pageNum, Integer pageSize, String keyword);
}
