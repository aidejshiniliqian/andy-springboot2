package com.andy.warehouse.dto.material;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialDTO {

    private Long id;
    private String materialCode;
    private String materialName;
    private String specification;
    private String model;
    private Long categoryId;
    private String categoryName;
    private String unit;
    private String barcode;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal safetyStock;
    private BigDecimal maxStock;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
