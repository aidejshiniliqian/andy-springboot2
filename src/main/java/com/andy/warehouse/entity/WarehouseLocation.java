package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wms_warehouse_location")
@Getter
@Setter
public class WarehouseLocation extends BaseEntity {

    @Column(name = "location_code", nullable = false, length = 50)
    private String locationCode;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private WarehouseZone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "capacity", precision = 15, scale = 2)
    private BigDecimal capacity;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "max_weight", precision = 10, scale = 2)
    private BigDecimal maxWeight;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();
}
