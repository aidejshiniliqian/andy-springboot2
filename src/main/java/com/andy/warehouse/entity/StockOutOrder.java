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
@TableName("wh_stock_out_order")
public class StockOutOrder extends BaseEntity {

    private String orderNo;

    private Integer orderType;

    private Long warehouseId;

    private String receiver;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    private Integer status;

    private String remark;

    private Long operatorId;

    private String operatorName;

    @TableField(exist = false)
    @Builder.Default
    private List<StockOutOrderItem> items = new ArrayList<>();

    @TableField(exist = false)
    private Warehouse warehouse;
}
