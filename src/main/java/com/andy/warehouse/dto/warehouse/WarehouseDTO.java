package com.andy.warehouse.dto.warehouse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WarehouseDTO {

    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String description;
    private String address;
    private BigDecimal area;
    private BigDecimal capacity;
    private Long managerId;
    private String managerName;
    private String contactPhone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
