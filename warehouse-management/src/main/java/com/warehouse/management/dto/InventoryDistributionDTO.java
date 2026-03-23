package com.warehouse.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 库存分布DTO
 * 用于可视化看板展示各仓库的库存分布情况
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDistributionDTO {
    /** 仓库ID */
    private Long warehouseId;
    /** 仓库名称 */
    private String warehouseName;
    /** 物料分类ID */
    private Long categoryId;
    /** 物料分类名称 */
    private String categoryName;
    /** 库存数量 */
    private BigDecimal quantity;
    /** 库存金额 */
    private BigDecimal amount;
    /** 占比（百分比） */
    private BigDecimal percentage;
}
