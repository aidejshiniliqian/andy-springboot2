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
public class StockInCreateRequest {

    @NotBlank(message = "入库类型不能为空")
    @Size(max = 20, message = "入库类型长度不能超过20")
    private String orderType;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Size(max = 100, message = "供应商名称长度不能超过100")
    private String supplierName;

    @Size(max = 50, message = "供应商联系人长度不能超过50")
    private String supplierContact;

    @Size(max = 20, message = "供应商电话长度不能超过20")
    private String supplierPhone;

    private LocalDate expectedDate;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    @NotEmpty(message = "入库明细不能为空")
    @Valid
    private List<StockInItemRequest> items;

    @Data
    public static class StockInItemRequest {

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

        private LocalDate productionDate;

        private LocalDate expiryDate;

        @Size(max = 200, message = "备注长度不能超过200")
        private String remark;
    }
}
