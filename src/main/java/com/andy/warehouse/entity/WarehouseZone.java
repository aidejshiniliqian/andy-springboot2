package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TableName("wms_warehouse_zone")
@Getter
@Setter
public class WarehouseZone extends BaseEntity {

    @TableField("zone_code")
    private String zoneCode;

    @TableField("zone_name")
    private String zoneName;

    @TableField("zone_type")
    private String zoneType;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("area")
    private BigDecimal area;

    @TableField("capacity")
    private BigDecimal capacity;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private List<WarehouseLocation> locations = new ArrayList<>();
}
