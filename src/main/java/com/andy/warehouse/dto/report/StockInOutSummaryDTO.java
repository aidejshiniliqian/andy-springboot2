package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockInOutSummaryDTO {

    private String period;

    private Long warehouseId;

    private String warehouseName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String categoryName;

    private BigDecimal beginQuantity;

    private BigDecimal stockInQuantity;

    private BigDecimal stockOutQuantity;

    private BigDecimal endQuantity;

    private BigDecimal beginAmount;

    private BigDecimal stockInAmount;

    private BigDecimal stockOutAmount;

    private BigDecimal endAmount;
}
