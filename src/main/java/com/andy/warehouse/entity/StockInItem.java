package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("wms_stock_in_item")
@Getter
@Setter
public class StockInItem extends BaseEntity {

    @TableField("stock_in_order_id")
    private Long stockInOrderId;

    @TableField("material_id")
    private Long materialId;

    @TableField("location_id")
    private Long locationId;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("actual_quantity")
    private BigDecimal actualQuantity;

    @TableField("unit")
    private String unit;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDate productionDate;

    @TableField("expiry_date")
    private LocalDate expiryDate;

    @TableField("remark")
    private String remark;

    @TableField("status")
    private String status;

    @TableField(exist = false)
    private StockInOrder stockInOrder;

    @TableField(exist = false)
    private Material material;

    @TableField(exist = false)
    private WarehouseLocation location;
}
