package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TableName("wms_warehouse_location")
@Getter
@Setter
public class WarehouseLocation extends BaseEntity {

    @TableField("location_code")
    private String locationCode;

    @TableField("location_name")
    private String locationName;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("capacity")
    private BigDecimal capacity;

    @TableField("length")
    private BigDecimal length;

    @TableField("width")
    private BigDecimal width;

    @TableField("height")
    private BigDecimal height;

    @TableField("max_weight")
    private BigDecimal maxWeight;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private WarehouseZone zone;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private List<Inventory> inventories = new ArrayList<>();
}
