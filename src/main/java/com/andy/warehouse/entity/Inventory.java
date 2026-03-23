package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("wms_inventory")
@Getter
@Setter
public class Inventory extends BaseEntity {

    @TableField("material_id")
    private Long materialId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("location_id")
    private Long locationId;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("available_quantity")
    private BigDecimal availableQuantity;

    @TableField("locked_quantity")
    private BigDecimal lockedQuantity;

    @TableField("unit")
    private String unit;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDate productionDate;

    @TableField("expiry_date")
    private LocalDate expiryDate;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private Material material;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private WarehouseLocation location;
}
