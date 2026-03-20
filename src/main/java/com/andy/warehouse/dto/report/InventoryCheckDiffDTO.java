package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryCheckDiffDTO {

    private String checkNo;

    private LocalDateTime checkTime;

    private Long warehouseId;

    private String warehouseName;

    private String locationCode;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String batchNo;

    private BigDecimal systemQuantity;

    private BigDecimal actualQuantity;

    private BigDecimal diffQuantity;

    private BigDecimal diffAmount;

    private String diffType;

    private String operatorName;

    private String remark;
}
