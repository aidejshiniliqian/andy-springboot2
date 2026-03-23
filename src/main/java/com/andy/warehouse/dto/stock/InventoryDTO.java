package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryDTO {

    private Long id;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private String locationName;
    private BigDecimal quantity;
    private BigDecimal availableQuantity;
    private BigDecimal lockedQuantity;
    private String unit;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
