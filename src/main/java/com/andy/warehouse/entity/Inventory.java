package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wms_inventory")
@Getter
@Setter
public class Inventory extends BaseEntity {

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

    @Column(name = "available_quantity", precision = 15, scale = 2)
    private BigDecimal availableQuantity;

    @Column(name = "locked_quantity", precision = 15, scale = 2)
    private BigDecimal lockedQuantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Column(name = "production_date")
    private java.time.LocalDate productionDate;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;

    @Column(name = "status")
    private Integer status = 1;
}
