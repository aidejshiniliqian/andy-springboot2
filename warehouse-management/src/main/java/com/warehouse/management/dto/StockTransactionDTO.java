package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存收发存明细DTO
 * 用于展示入库、出库的明细记录，形成收发存流水账
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionDTO {
    /** 单据编号 */
    private String orderNo;
    /** 交易类型：入库/出库 */
    private String transactionType;
    /** 交易时间 */
    private LocalDateTime transactionTime;
    /** 仓库名称 */
    private String warehouseName;
    /** 物料名称 */
    private String materialName;
    /** 物料编码 */
    private String materialCode;
    /** 计量单位 */
    private String unit;
    /** 交易数量 */
    private BigDecimal quantity;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 交易金额 */
    private BigDecimal totalPrice;
    /** 经办人 */
    private String operator;
    /** 备注 */
    private String remark;
}
