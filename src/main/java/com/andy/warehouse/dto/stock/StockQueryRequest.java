package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockQueryRequest {

    private String orderNo;
    private String orderType;
    private Long warehouseId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 0;
    private Integer size = 10;
}
