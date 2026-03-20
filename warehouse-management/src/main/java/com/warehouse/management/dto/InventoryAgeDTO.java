package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库龄分析DTO
 * 用于分析库存物料的存放时间，帮助识别滞销物料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAgeDTO {
    /** 仓库ID */
    private Long warehouseId;
    /** 仓库名称 */
    private String warehouseName;
    /** 物料ID */
    private Long materialId;
    /** 物料名称 */
    private String materialName;
    /** 物料编码 */
    private String materialCode;
    /** 计量单位 */
    private String unit;
    /** 库存数量 */
    private BigDecimal quantity;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 库存总金额 */
    private BigDecimal totalPrice;
    /** 首次入库时间 */
    private LocalDateTime firstInTime;
    /** 库龄（天数） */
    private Long daysInStock;
    /** 库龄等级：如"0-30天"、"30-90天"、"90天以上" */
    private String ageLevel;
}
