package com.andy.warehouse.dto.organization;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizationUpdateRequest {

    @Size(max = 100, message = "机构名称长度不能超过100")
    private String orgName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @Size(max = 200, message = "地址长度不能超过200")
    private String address;

    @Size(max = 50, message = "联系人长度不能超过50")
    private String contactPerson;

    @Size(max = 20, message = "联系电话长度不能超过20")
    private String contactPhone;

    private Integer status;
}
