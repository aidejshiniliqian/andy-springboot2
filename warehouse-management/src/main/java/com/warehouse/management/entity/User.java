package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    @TableField
    private String username;

    @TableField
    private String password;

    @TableField
    private String realName;

    @TableField
    private String phone;

    @TableField
    private String email;

    private Integer status;

    @TableField("department_id")
    private Long departmentId;

    @TableField(exist = false)
    private Department department;

    @TableField(exist = false)
    private Set<Role> roles;
}
