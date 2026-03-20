package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RoleUpdateRequest {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    @Size(max = 50, message = "角色名称长度不能超过50")
    private String name;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Set<Long> permissionIds;

    private Integer status;
}
