package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialCreateRequest {

    @NotBlank(message = "物资名称不能为空")
    @Size(max = 100, message = "物资名称长度不能超过100")
    private String name;

    @Size(max = 50, message = "物资编码长度不能超过50")
    private String code;

    @Size(max = 100, message = "条码长度不能超过100")
    private String barcode;

    @Size(max = 200, message = "规格长度不能超过200")
    private String specification;

    @Size(max = 100, message = "型号长度不能超过100")
    private String model;

    @Size(max = 20, message = "单位长度不能超过20")
    private String unit;

    private BigDecimal price;

    private Integer safetyStock;

    private Integer maxStock;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Long categoryId;

    private Integer status = 1;
}
