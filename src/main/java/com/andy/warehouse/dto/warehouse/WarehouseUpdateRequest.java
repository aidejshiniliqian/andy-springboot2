package com.andy.warehouse.dto.warehouse;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseUpdateRequest {

    @Size(max = 100, message = "仓库名称长度不能超过100")
    private String warehouseName;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @Size(max = 200, message = "地址长度不能超过200")
    private String address;

    private BigDecimal area;

    private BigDecimal capacity;

    private Long managerId;

    @Size(max = 20, message = "联系电话长度不能超过20")
    private String contactPhone;

    private Integer status;
}
