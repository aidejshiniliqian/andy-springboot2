package com.andy.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseUpdateRequest {

    @NotNull(message = "仓库ID不能为空")
    private Long id;

    @Size(max = 100, message = "仓库名称长度不能超过100")
    private String name;

    @Size(max = 255, message = "地址长度不能超过255")
    private String address;

    private BigDecimal area;

    private Integer capacity;

    private Long managerId;

    @Size(max = 50, message = "负责人姓名长度不能超过50")
    private String managerName;

    @Size(max = 20, message = "电话长度不能超过20")
    private String phone;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private Integer status;
}
