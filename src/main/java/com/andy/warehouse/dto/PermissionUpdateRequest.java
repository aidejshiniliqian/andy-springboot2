package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionUpdateRequest {

    @NotNull(message = "权限ID不能为空")
    private Long id;

    @Size(max = 50, message = "权限名称长度不能超过50")
    private String name;

    @Size(max = 255, message = "路径长度不能超过255")
    private String path;

    @Size(max = 255, message = "组件长度不能超过255")
    private String component;

    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    private Integer sortOrder;

    private Integer status;
}
