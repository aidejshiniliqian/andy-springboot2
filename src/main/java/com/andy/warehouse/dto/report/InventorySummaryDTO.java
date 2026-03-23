package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventorySummaryDTO {

    private Long warehouseId;

    private String warehouseName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String categoryName;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal availableQuantity;

    private BigDecimal lockedQuantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal safetyStock;

    private String stockStatus;
}
