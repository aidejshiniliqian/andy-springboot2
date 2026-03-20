package com.andy.warehouse.dto.role;

import lombok.Data;

@Data
public class RoleQueryRequest {

    private String roleCode;
    private String roleName;
    private Integer status;
    private Integer page = 0;
    private Integer size = 10;
}
