package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TableName("sys_user")
@Getter
@Setter
public class User extends BaseEntity {

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableField("gender")
    private Integer gender;

    @TableField("org_id")
    private Long orgId;

    @TableField("dept_id")
    private Long deptId;

    @TableField("status")
    private Integer status = 1;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField(exist = false)
    private Organization organization;

    @TableField(exist = false)
    private Department department;

    @TableField(exist = false)
    private List<Role> roles = new ArrayList<>();
}
