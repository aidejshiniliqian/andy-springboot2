package com.andy.warehouse.dto.organization;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationDTO {

    private Long id;
    private String orgCode;
    private String orgName;
    private String description;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
