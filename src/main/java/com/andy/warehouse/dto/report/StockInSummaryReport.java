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
public class StockInSummaryReport {

    private Long orderId;
    private String orderNo;
    private Integer orderType;
    private String orderTypeName;
    private String warehouseName;
    private String supplier;
    private Integer itemCount;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private Integer status;
    private String statusName;
    private String operatorName;
}
