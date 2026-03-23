package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_out_stock")
public class OutStock extends BaseEntity {
    @TableField
    private String orderNo;

    private String receiver;

    private String receiverPhone;

    private String receiverAddress;

    private Integer type;

    private LocalDateTime outStockTime;

    private String remark;

    private Integer status;

    private Long warehouseId;

    private Long operatorId;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private User operator;

    @TableField(exist = false)
    private List<OutStockDetail> details;
}
