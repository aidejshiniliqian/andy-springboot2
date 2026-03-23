package com.andy.warehouse.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionCreateRequest {

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 50, message = "权限编码长度不能超过50")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100")
    private String permissionName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @Size(max = 20, message = "类型长度不能超过20")
    private String type;

    @Size(max = 200, message = "资源URL长度不能超过200")
    private String resourceUrl;

    @Size(max = 10, message = "HTTP方法长度不能超过10")
    private String httpMethod;

    private Long parentId;

    private Integer sortOrder = 0;

    @Size(max = 50, message = "图标长度不能超过50")
    private String icon;
}
