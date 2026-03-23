package com.andy.warehouse.dto.material;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialUpdateRequest {

    @Size(max = 100, message = "物资名称长度不能超过100")
    private String materialName;

    @Size(max = 200, message = "规格长度不能超过200")
    private String specification;

    @Size(max = 50, message = "型号长度不能超过50")
    private String model;

    private Long categoryId;

    @Size(max = 20, message = "单位长度不能超过20")
    private String unit;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    private BigDecimal safetyStock;

    private BigDecimal maxStock;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Integer status;
}
