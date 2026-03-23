package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {
    private String name;

    private String code;

    private String url;

    private String method;

    private Integer type;

    private Integer sort;

    private Integer status;

    private Long parentId;

    @TableField(exist = false)
    private Permission parent;

    @TableField(exist = false)
    private List<Permission> children;
}
