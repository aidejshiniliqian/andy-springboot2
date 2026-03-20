package com.andy.warehouse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockOutOrderCreateRequest {

    @NotNull(message = "出库类型不能为空")
    private Integer orderType;

    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    private String receiver;

    private LocalDateTime orderDate;

    private String remark;

    @NotEmpty(message = "出库明细不能为空")
    @Valid
    private List<StockOutOrderItemRequest> items;
}
