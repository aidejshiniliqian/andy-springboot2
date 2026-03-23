package com.andy.warehouse.dto.material;

import lombok.Data;

@Data
public class MaterialQueryRequest {

    private String materialCode;
    private String materialName;
    private Long categoryId;
    private Integer status;
    private Integer page = 0;
    private Integer size = 10;
}
