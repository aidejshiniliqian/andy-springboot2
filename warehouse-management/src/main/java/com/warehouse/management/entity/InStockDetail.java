package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_in_stock_detail")
public class InStockDetail extends BaseEntity {
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String remark;

    private Long inStockId;

    private Long materialId;

    @TableField(exist = false)
    private InStock inStock;

    @TableField(exist = false)
    private Material material;
}
