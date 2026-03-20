package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialCategoryCreateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;

    @Size(max = 50, message = "分类编码长度不能超过50")
    private String code;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Long parentId;

    private Integer status = 1;
}
