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

@TableName("wms_stock_in_order")
@Getter
@Setter
public class StockInOrder extends BaseEntity {

    @TableField("order_no")
    private String orderNo;

    @TableField("order_type")
    private String orderType;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("supplier_contact")
    private String supplierContact;

    @TableField("supplier_phone")
    private String supplierPhone;

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

    @TableField("remark")
    private String remark;

    @TableField("status")
    private String status;

    @TableField(exist = false)
    private Warehouse warehouse;

    @TableField(exist = false)
    private User operator;

    @TableField(exist = false)
    private List<StockInItem> items = new ArrayList<>();
}
