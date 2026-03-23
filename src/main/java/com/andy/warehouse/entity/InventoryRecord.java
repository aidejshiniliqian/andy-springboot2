package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@TableName("wms_inventory_record")
@Getter
@Setter
public class InventoryRecord extends BaseEntity {

    @TableField("record_no")
    private String recordNo;

    @TableField("record_type")
    private String recordType;

    @TableField("biz_type")
    private String bizType;

    @TableField("biz_no")
    private String bizNo;

    @TableField("material_id")
    private Long materialId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("location_id")
    private Long locationId;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("before_quantity")
    private BigDecimal beforeQuantity;

    @TableField("after_quantity")
    private BigDecimal afterQuantity;

    @TableField("unit")
    private String unit;

    @TableField("batch_no")
    private String batchNo;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    private Material material;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private WarehouseLocation location;

    @TableField(exist = false)
    private User operator;
}
