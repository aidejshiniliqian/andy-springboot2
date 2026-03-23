package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@TableName("sys_department")
@Getter
@Setter
public class Department extends BaseEntity {

    @TableField("dept_code")
    private String deptCode;

    @TableField("dept_name")
    private String deptName;

    @TableField("description")
    private String description;

    @TableField("parent_id")
    private Long parentId;

    @TableField("org_id")
    private Long orgId;

    @TableField("sort_order")
    private Integer sortOrder = 0;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private Department parent;

    @TableField(exist = false)
    private List<Department> children = new ArrayList<>();

    @TableField(exist = false)
    private Organization organization;

    @TableField(exist = false)
    private List<User> users = new ArrayList<>();
}
