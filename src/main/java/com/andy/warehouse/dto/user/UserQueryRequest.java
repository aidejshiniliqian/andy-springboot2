package com.andy.warehouse.dto.user;

import lombok.Data;

@Data
public class UserQueryRequest {

    private String username;
    private String realName;
    private Long orgId;
    private Long deptId;
    private Integer status;
    private Integer page = 0;
    private Integer size = 10;
}
