package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentUpdateRequest {

    @NotNull(message = "部门ID不能为空")
    private Long id;

    @Size(max = 100, message = "部门名称长度不能超过100")
    private String name;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Long parentId;

    private Integer status;
}
