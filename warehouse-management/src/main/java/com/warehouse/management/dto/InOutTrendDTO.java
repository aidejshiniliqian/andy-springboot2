package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出入库趋势DTO
 * 用于可视化看板展示一段时间内的出入库趋势
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InOutTrendDTO {
    /** 统计日期 */
    private LocalDate date;
    /** 入库数量 */
    private BigDecimal inQuantity;
    /** 入库金额 */
    private BigDecimal inAmount;
    /** 出库数量 */
    private BigDecimal outQuantity;
    /** 出库金额 */
    private BigDecimal outAmount;
    /** 净变化数量 */
    private BigDecimal netQuantity;
}
