package com.andy.warehouse.dto.department;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DepartmentDTO {

    private Long id;
    private String deptCode;
    private String deptName;
    private String description;
    private Long parentId;
    private String parentName;
    private Long orgId;
    private String orgName;
    private Integer sortOrder;
    private Integer status;
    private List<DepartmentDTO> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
