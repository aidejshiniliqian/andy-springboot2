package com.andy.warehouse.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryVarianceReport {

    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private String warehouseName;
    private String position;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private Integer varianceQuantity;
    private BigDecimal varianceRate;
    private BigDecimal unitPrice;
    private BigDecimal varianceAmount;
    private String varianceType;
}
