package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("wh_stock")
public class Stock extends BaseEntity {

    private Long warehouseId;

    private Long materialId;

    private Integer quantity;

    private Integer availableQuantity;

    private Integer lockedQuantity;

    private String batchNo;

    private String position;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private Material material;
}
