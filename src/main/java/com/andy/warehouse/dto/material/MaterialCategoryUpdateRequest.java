package com.andy.warehouse.dto.material;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialCategoryUpdateRequest {

    @Size(max = 100, message = "分类名称长度不能超过100")
    private String categoryName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Long parentId;

    private Integer sortOrder;

    private Integer status;
}
