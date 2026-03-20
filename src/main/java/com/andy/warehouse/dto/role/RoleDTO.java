package com.andy.warehouse.dto.role;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleDTO {

    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
    private List<String> permissionNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
