package com.andy.warehouse.dto.stock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class StockOutCreateRequest {

    @NotBlank(message = "出库类型不能为空")
    @Size(max = 20, message = "出库类型长度不能超过20")
    private String orderType;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Size(max = 100, message = "领用人长度不能超过100")
    private String recipientName;

    @Size(max = 100, message = "领用部门长度不能超过100")
    private String recipientDept;

    @Size(max = 50, message = "联系人长度不能超过50")
    private String recipientContact;

    @Size(max = 20, message = "联系电话长度不能超过20")
    private String recipientPhone;

    private LocalDate expectedDate;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    @NotEmpty(message = "出库明细不能为空")
    @Valid
    private List<StockOutItemRequest> items;

    @Data
    public static class StockOutItemRequest {

        @NotNull(message = "物资不能为空")
        private Long materialId;

        private Long locationId;

        @NotNull(message = "数量不能为空")
        private BigDecimal quantity;

        @Size(max = 20, message = "单位长度不能超过20")
        private String unit;

        private BigDecimal unitPrice;

        @Size(max = 50, message = "批次号长度不能超过50")
        private String batchNo;

        @Size(max = 200, message = "备注长度不能超过200")
        private String remark;
    }
}
