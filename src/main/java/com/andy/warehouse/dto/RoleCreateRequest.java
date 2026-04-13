package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RoleCreateRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String name;

    @Size(max = 50, message = "角色编码长度不能超过50")
    private String code;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Set<Long> permissionIds;

    private Integer status = 1;
}
