package com.andy.warehouse.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockOutOrderDTO {

    private Long id;
    private String orderNo;
    private String orderType;
    private Long warehouseId;
    private String warehouseName;
    private String recipientName;
    private String recipientDept;
    private String recipientContact;
    private String recipientPhone;
    private BigDecimal totalAmount;
    private BigDecimal totalQuantity;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDateTime actualDate;
    private Long operatorId;
    private String operatorName;
    private Long approverId;
    private String approverName;
    private LocalDateTime approveTime;
    private String remark;
    private String status;
    private List<StockOutItemDTO> items;
    private LocalDateTime createdAt;
}
