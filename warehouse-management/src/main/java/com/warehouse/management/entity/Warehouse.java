package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_warehouse")
public class Warehouse extends BaseEntity {
    @TableField
    private String name;

    @TableField
    private String code;

    @TableField
    private String address;

    @TableField
    private String manager;

    @TableField
    private String phone;

    @TableField
    private String remark;

    private Integer status;

    private Integer sort;
}
