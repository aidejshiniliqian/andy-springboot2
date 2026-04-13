package com.andy.warehouse.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelvingEfficiencyReport {

    private Long orderId;
    private String orderNo;
    private String warehouseName;
    private String operatorName;
    private Integer totalItems;
    private Integer completedItems;
    private BigDecimal completionRate;
    private Integer totalQuantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    private BigDecimal efficiency;
}
