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
public class StockDistributionVO {

    private Long warehouseId;
    private String warehouseName;
    private Integer materialCount;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private BigDecimal utilizationRate;
    private Integer capacity;
}
