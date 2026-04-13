package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wh_stock_out_order_item")
public class StockOutOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long materialId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String batchNo;

    private String remark;

    @TableField(exist = false)
    private StockOutOrder order;

    @TableField(exist = false)
    private Material material;
}
