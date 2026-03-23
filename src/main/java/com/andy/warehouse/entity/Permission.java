package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@TableName("sys_permission")
@Getter
@Setter
public class Permission extends BaseEntity {

    @TableField("permission_code")
    private String permissionCode;

    @TableField("permission_name")
    private String permissionName;

    @TableField("description")
    private String description;

    @TableField("type")
    private String type;

    @TableField("resource_url")
    private String resourceUrl;

    @TableField("http_method")
    private String httpMethod;

    @TableField("parent_id")
    private Long parentId;

    @TableField("sort_order")
    private Integer sortOrder = 0;

    @TableField("icon")
    private String icon;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private Permission parent;

    @TableField(exist = false)
    private List<Permission> children = new ArrayList<>();

    @TableField(exist = false)
    private List<Role> roles = new ArrayList<>();
}
