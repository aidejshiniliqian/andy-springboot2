package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String email;

    private String phone;

    private String avatar;

    private Integer status;

    private Long orgId;

    private Long deptId;

    @TableField(exist = false)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @TableField(exist = false)
    private Organization organization;

    @TableField(exist = false)
    private Department department;
}
