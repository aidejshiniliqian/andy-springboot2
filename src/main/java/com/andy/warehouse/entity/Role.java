package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@TableName("sys_role")
@Getter
@Setter
public class Role extends BaseEntity {

    @TableField("role_code")
    private String roleCode;

    @TableField("role_name")
    private String roleName;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private List<Permission> permissions = new ArrayList<>();

    @TableField(exist = false)
    private List<User> users = new ArrayList<>();
}
