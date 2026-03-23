package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {
    @TableField
    private String name;

    @TableField
    private String code;

    @TableField
    private String description;

    private Integer status;

    @TableField(exist = false)
    private Set<Permission> permissions;
}
