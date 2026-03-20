package com.andy.warehouse.dto.department;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentUpdateRequest {

    @Size(max = 100, message = "部门名称长度不能超过100")
    private String deptName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Long parentId;

    private Integer sortOrder;

    private Integer status;
}
