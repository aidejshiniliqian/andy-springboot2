package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("wh_warehouse")
public class Warehouse extends BaseEntity {

    private String name;

    private String code;

    private String address;

    private BigDecimal area;

    private Integer capacity;

    private Long managerId;

    private String managerName;

    private String phone;

    private String description;

    private Integer status;

    private Long orgId;

    @TableField(exist = false)
    private Organization organization;
}
