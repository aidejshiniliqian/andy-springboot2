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
public class StockAgeAnalysis {

    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private String warehouseName;
    private String batchNo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private LocalDateTime inboundDate;
    private Integer stockAgeDays;
    private String ageRange;
    private String position;
}
