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
public class StockTransactionDetail {

    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private String warehouseName;
    private String transactionType;
    private String orderNo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String batchNo;
    private LocalDateTime transactionTime;
    private Integer beforeQuantity;
    private Integer afterQuantity;
}
