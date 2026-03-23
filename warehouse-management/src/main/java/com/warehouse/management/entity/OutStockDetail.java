package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_out_stock_detail")
public class OutStockDetail extends BaseEntity {
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String remark;

    private Long outStockId;

    private Long materialId;

    @TableField(exist = false)
    private OutStock outStock;

    @TableField(exist = false)
    private Material material;
}
