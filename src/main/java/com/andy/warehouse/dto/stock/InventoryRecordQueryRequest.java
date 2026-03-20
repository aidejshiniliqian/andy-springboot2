package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryRecordQueryRequest {

    private String recordNo;
    private String recordType;
    private Long materialId;
    private Long warehouseId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page = 0;
    private Integer size = 10;
}
