package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PickingEfficiencyDTO {

    private LocalDate workDate;

    private Long operatorId;

    private String operatorName;

    private Long warehouseId;

    private String warehouseName;

    private Integer totalOrders;

    private Integer totalItems;

    private BigDecimal totalQuantity;

    private BigDecimal totalTimeMinutes;

    private BigDecimal itemsPerHour;

    private BigDecimal quantityPerHour;

    private BigDecimal avgTimePerOrder;

    private BigDecimal accuracyRate;
}
