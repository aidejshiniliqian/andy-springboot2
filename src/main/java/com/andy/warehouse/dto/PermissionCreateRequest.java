package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionCreateRequest {

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50")
    private String name;

    @Size(max = 100, message = "权限编码长度不能超过100")
    private String code;

    @NotBlank(message = "权限类型不能为空")
    private Integer type;

    private Long parentId;

    @Size(max = 255, message = "路径长度不能超过255")
    private String path;

    @Size(max = 255, message = "组件长度不能超过255")
    private String component;

    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    private Integer sortOrder = 0;

    private Integer status = 1;
}
