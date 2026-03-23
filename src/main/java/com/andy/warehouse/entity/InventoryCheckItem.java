package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wh_inventory_check_item")
public class InventoryCheckItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long checkId;

    private Long materialId;

    private String position;

    private Integer systemQuantity;

    private Integer actualQuantity;

    private Integer varianceQuantity;

    private BigDecimal unitPrice;

    private BigDecimal varianceAmount;

    private String varianceType;

    private String remark;

    @TableField(exist = false)
    private InventoryCheck inventoryCheck;

    @TableField(exist = false)
    private Material material;
}
