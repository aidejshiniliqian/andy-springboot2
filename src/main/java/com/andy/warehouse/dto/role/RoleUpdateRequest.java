package com.andy.warehouse.dto.role;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {

    @Size(max = 100, message = "角色名称长度不能超过100")
    private String roleName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Integer status;

    private List<Long> permissionIds;
}
