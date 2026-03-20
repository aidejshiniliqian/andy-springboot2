package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.stock.StockInCreateRequest;
import com.andy.warehouse.dto.stock.StockInOrderDTO;
import com.andy.warehouse.dto.stock.StockQueryRequest;

public interface StockInService {

    StockInOrderDTO createStockIn(StockInCreateRequest request);

    StockInOrderDTO confirmStockIn(Long id);

    StockInOrderDTO cancelStockIn(Long id);

    StockInOrderDTO getStockInById(Long id);

    StockInOrderDTO getStockInByOrderNo(String orderNo);

    PageResult<StockInOrderDTO> getStockInList(StockQueryRequest request);
}
