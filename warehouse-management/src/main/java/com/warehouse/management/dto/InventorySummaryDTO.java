package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 库存汇总报表DTO
 * 用于展示当前库存的汇总信息，包括仓库、物料、数量、金额等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDTO {
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
    /** 物料分类名称 */
    private String categoryName;
    /** 计量单位 */
    private String unit;
    /** 库存数量 */
    private BigDecimal quantity;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 库存总金额 */
    private BigDecimal totalPrice;
}
