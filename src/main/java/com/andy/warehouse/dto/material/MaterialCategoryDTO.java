package com.andy.warehouse.dto.material;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaterialCategoryDTO {

    private Long id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Long parentId;
    private String parentName;
    private Integer sortOrder;
    private Integer status;
    private List<MaterialCategoryDTO> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
