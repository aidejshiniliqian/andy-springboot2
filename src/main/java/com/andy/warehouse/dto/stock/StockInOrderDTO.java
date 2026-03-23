package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockInOrderDTO {

    private Long id;
    private String orderNo;
    private String orderType;
    private Long warehouseId;
    private String warehouseName;
    private String supplierName;
    private String supplierContact;
    private String supplierPhone;
    private BigDecimal totalAmount;
    private BigDecimal totalQuantity;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDateTime actualDate;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private String status;
    private List<StockInItemDTO> items;
    private LocalDateTime createdAt;
}
