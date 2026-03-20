package com.andy.warehouse.dto.warehouse;

import lombok.Data;

@Data
public class WarehouseQueryRequest {

    private String warehouseCode;
    private String warehouseName;
    private Integer status;
    private Integer page = 0;
    private Integer size = 10;
}
