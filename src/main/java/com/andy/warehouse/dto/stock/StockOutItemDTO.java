package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutItemDTO {

    private Long id;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private Long locationId;
    private String locationCode;
    private String locationName;
    private BigDecimal quantity;
    private BigDecimal actualQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String batchNo;
    private String remark;
    private String status;
}
