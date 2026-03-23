package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.StockOutCreateRequest;
import com.andy.warehouse.dto.stock.StockOutOrderDTO;
import com.andy.warehouse.dto.stock.StockQueryRequest;

public interface StockOutService {

    StockOutOrderDTO createStockOut(StockOutCreateRequest request);

    StockOutOrderDTO confirmStockOut(Long id);

    StockOutOrderDTO approveStockOut(Long id);

    StockOutOrderDTO cancelStockOut(Long id);

    StockOutOrderDTO getStockOutById(Long id);

    StockOutOrderDTO getStockOutByOrderNo(String orderNo);

    PageResult<StockOutOrderDTO> getStockOutList(StockQueryRequest request);
}
