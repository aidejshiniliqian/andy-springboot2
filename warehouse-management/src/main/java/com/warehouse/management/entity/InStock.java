package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_in_stock")
public class InStock extends BaseEntity {
    @TableField
    private String orderNo;

    private String supplier;

    private String supplierPhone;

    private String supplierAddress;

    private Integer type;

    private LocalDateTime inStockTime;

    private String remark;

    private Integer status;

    private Long warehouseId;

    private Long operatorId;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private User operator;

    @TableField(exist = false)
    private List<InStockDetail> details;
}
