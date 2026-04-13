package com.andy.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutOrderItemRequest {

    @NotNull(message = "物资ID不能为空")
    private Long materialId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    private BigDecimal unitPrice;

    private String batchNo;

    private String remark;
}
