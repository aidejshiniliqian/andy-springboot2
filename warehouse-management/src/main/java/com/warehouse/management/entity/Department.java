package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class Department extends BaseEntity {
    @TableField
    private String name;

    @TableField
    private String description;

    private Integer sort;

    private Integer status;

    @TableField("parent_id")
    private Long parentId;

    @TableField(exist = false)
    private Department parent;

    @TableField(exist = false)
    private List<Department> children;

    @TableField("organization_id")
    private Long organizationId;

    @TableField(exist = false)
    private Organization organization;
}
