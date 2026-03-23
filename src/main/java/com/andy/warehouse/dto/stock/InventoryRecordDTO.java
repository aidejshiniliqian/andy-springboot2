package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryRecordDTO {

    private Long id;
    private String recordNo;
    private String recordType;
    private String bizType;
    private String bizNo;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private BigDecimal quantity;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private String unit;
    private String batchNo;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createdAt;
}
