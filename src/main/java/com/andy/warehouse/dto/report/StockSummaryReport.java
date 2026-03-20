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
public class StockSummaryReport {

    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private String categoryName;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer lockedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private Integer safetyStock;
    private String stockStatus;
}
