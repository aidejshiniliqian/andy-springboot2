package com.andy.warehouse.dto.organization;

import lombok.Data;

@Data
public class OrganizationQueryRequest {

    private String orgCode;
    private String orgName;
    private Integer status;
    private Integer page = 0;
    private Integer size = 10;
}
