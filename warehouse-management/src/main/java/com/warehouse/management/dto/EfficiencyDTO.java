package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作业效率DTO
 * 用于统计拣货、上架等作业的效率数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyDTO {
    /** 统计日期 */
    private LocalDate date;
    /** 操作人员 */
    private String operator;
    /** 完成单据数量 */
    private Long orderCount;
    /** 完成总数量 */
    private BigDecimal totalQuantity;
    /** 效率值（如：件/小时） */
    private BigDecimal efficiencyRate;
    /** 作业类型：拣货/上架/盘点等 */
    private String operationType;
}
