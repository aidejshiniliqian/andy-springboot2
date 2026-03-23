package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@TableName("wms_stock_out_item")
@Getter
@Setter
public class StockOutItem extends BaseEntity {

    @TableField("stock_out_order_id")
    private Long stockOutOrderId;

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

    @TableField("remark")
    private String remark;

    @TableField("status")
    private String status;

    @TableField(exist = false)
    private StockOutOrder stockOutOrder;

    @TableField(exist = false)
    private Material material;

    @TableField(exist = false)
    private WarehouseLocation location;
}
