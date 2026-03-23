package com.andy.warehouse.dto.stock;

import lombok.Data;

@Data
public class InventoryQueryRequest {

    private Long materialId;
    private Long warehouseId;
    private Long locationId;
    private String batchNo;
    private Integer page = 0;
    private Integer size = 10;
}
