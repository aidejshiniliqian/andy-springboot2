package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InventoryAgeDTO {

    private Long warehouseId;

    private String warehouseName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String categoryName;

    private String batchNo;

    private LocalDate productionDate;

    private LocalDate expiryDate;

    private BigDecimal quantity;

    private String unit;

    private Integer ageDays;

    private String ageGroup;

    private BigDecimal amount;
}
