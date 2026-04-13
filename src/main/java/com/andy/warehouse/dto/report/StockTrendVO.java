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
public class StockTrendVO {

    private String date;
    private Integer inQuantity;
    private Integer outQuantity;
    private Integer netQuantity;
    private BigDecimal inAmount;
    private BigDecimal outAmount;
    private Integer orderCount;
}
