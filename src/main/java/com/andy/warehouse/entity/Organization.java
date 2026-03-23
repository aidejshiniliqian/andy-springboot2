package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@TableName("sys_organization")
@Getter
@Setter
public class Organization extends BaseEntity {

    @TableField("org_code")
    private String orgCode;

    @TableField("org_name")
    private String orgName;

    @TableField("description")
    private String description;

    @TableField("address")
    private String address;

    @TableField("contact_person")
    private String contactPerson;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private List<Department> departments = new ArrayList<>();

    @TableField(exist = false)
    private List<User> users = new ArrayList<>();
}
