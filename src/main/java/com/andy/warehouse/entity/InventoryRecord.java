package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wms_inventory_record")
@Getter
@Setter
public class InventoryRecord extends BaseEntity {

    @Column(name = "record_no", unique = true, nullable = false, length = 50)
    private String recordNo;

    @Column(name = "record_type", length = 20, nullable = false)
    private String recordType;

    @Column(name = "biz_type", length = 20)
    private String bizType;

    @Column(name = "biz_no", length = 50)
    private String bizNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @Column(name = "quantity", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "before_quantity", precision = 15, scale = 2)
    private BigDecimal beforeQuantity;

    @Column(name = "after_quantity", precision = 15, scale = 2)
    private BigDecimal afterQuantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    @Column(name = "remark", length = 500)
    private String remark;
}
