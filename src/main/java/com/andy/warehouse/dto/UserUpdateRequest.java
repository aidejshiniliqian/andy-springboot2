package com.andy.warehouse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(max = 50, message = "真实姓名长度不能超过50")
    private String realName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 20, message = "电话长度不能超过20")
    private String phone;

    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatar;

    private Long orgId;

    private Long deptId;

    private Set<Long> roleIds;

    private Integer status;
}
