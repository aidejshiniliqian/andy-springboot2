package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_organization")
public class Organization extends BaseEntity {
    private String name;

    private String description;

    private String address;

    private String contact;

    private String phone;

    private Integer sort;

    private Integer status;

    private Long parentId;

    @TableField(exist = false)
    private Organization parent;

    @TableField(exist = false)
    private List<Organization> children;
}
