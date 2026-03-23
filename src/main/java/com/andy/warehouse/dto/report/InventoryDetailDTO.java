package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryDetailDTO {

    private String recordNo;

    private String recordType;

    private String bizType;

    private String bizNo;

    private LocalDateTime recordTime;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private Long warehouseId;

    private String warehouseName;

    private String locationCode;

    private BigDecimal quantity;

    private BigDecimal beforeQuantity;

    private BigDecimal afterQuantity;

    private String unit;

    private String batchNo;

    private String operatorName;

    private String remark;
}
