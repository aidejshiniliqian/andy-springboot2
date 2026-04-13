package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("wh_inventory_check")
public class InventoryCheck extends BaseEntity {

    private String checkNo;

    private Long warehouseId;

    private LocalDateTime checkDate;

    private Integer status;

    private Integer totalItems;

    private Integer varianceItems;

    private BigDecimal totalVarianceAmount;

    private String remark;

    private Long operatorId;

    private String operatorName;

    @TableField(exist = false)
    @Builder.Default
    private List<InventoryCheckItem> items = new ArrayList<>();

    @TableField(exist = false)
    private Warehouse warehouse;
}
