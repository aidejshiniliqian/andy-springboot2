package com.andy.warehouse.dto.permission;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PermissionDTO {

    private Long id;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String type;
    private String resourceUrl;
    private String httpMethod;
    private Long parentId;
    private String parentName;
    private Integer sortOrder;
    private String icon;
    private Integer status;
    private List<PermissionDTO> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
