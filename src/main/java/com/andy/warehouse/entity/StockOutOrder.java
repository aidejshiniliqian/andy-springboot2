package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TableName("wms_stock_out_order")
@Getter
@Setter
public class StockOutOrder extends BaseEntity {

    @TableField("order_no")
    private String orderNo;

    @TableField("order_type")
    private String orderType;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("recipient_name")
    private String recipientName;

    @TableField("recipient_dept")
    private String recipientDept;

    @TableField("recipient_contact")
    private String recipientContact;

    @TableField("recipient_phone")
    private String recipientPhone;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("total_quantity")
    private BigDecimal totalQuantity;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("expected_date")
    private LocalDate expectedDate;

    @TableField("actual_date")
    private LocalDateTime actualDate;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("approver_id")
    private Long approverId;

    @TableField("approve_time")
    private LocalDateTime approveTime;

    @TableField("remark")
    private String remark;

    @TableField("status")
    private String status;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private User operator;

    @TableField(exist = false)
    private List<StockOutItem> items = new ArrayList<>();
}
