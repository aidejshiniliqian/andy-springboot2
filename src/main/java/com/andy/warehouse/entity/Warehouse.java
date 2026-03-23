package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TableName("wms_warehouse")
@Getter
@Setter
public class Warehouse extends BaseEntity {

    @TableField("warehouse_code")
    private String warehouseCode;

    @TableField("warehouse_name")
    private String warehouseName;

    @TableField("description")
    private String description;

    @TableField("address")
    private String address;

    @TableField("area")
    private BigDecimal area;

    @TableField("capacity")
    private BigDecimal capacity;

    @TableField("manager_id")
    private Long managerId;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private User manager;

    @TableField(exist = false)
    private List<WarehouseZone> zones = new ArrayList<>();

    @TableField(exist = false)
    private List<Inventory> inventories = new ArrayList<>();
}
