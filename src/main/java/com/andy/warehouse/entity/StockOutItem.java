package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wms_stock_out_item")
@Getter
@Setter
public class StockOutItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_order_id", nullable = false)
    private StockOutOrder stockOutOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @Column(name = "quantity", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "actual_quantity", precision = 15, scale = 2)
    private BigDecimal actualQuantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "status", length = 20)
    private String status;
}
