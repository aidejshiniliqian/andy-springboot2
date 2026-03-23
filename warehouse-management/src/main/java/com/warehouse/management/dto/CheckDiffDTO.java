package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盘点差异DTO
 * 用于展示盘点时系统库存与实际库存的差异情况
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckDiffDTO {
    /** 盘点单号 */
    private String checkNo;
    /** 盘点时间 */
    private LocalDateTime checkTime;
    /** 仓库名称 */
    private String warehouseName;
    /** 物料名称 */
    private String materialName;
    /** 物料编码 */
    private String materialCode;
    /** 计量单位 */
    private String unit;
    /** 系统库存数量 */
    private BigDecimal systemQty;
    /** 实际库存数量 */
    private BigDecimal actualQty;
    /** 差异数量 */
    private BigDecimal diffQty;
    /** 差异金额 */
    private BigDecimal diffAmount;
    /** 差异原因 */
    private String diffReason;
    /** 盘点人 */
    private String checker;
}
