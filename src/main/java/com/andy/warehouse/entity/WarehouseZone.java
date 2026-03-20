package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wms_warehouse_zone")
@Getter
@Setter
public class WarehouseZone extends BaseEntity {

    @Column(name = "zone_code", nullable = false, length = 50)
    private String zoneCode;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Column(name = "zone_type", length = 20)
    private String zoneType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "capacity", precision = 15, scale = 2)
    private BigDecimal capacity;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WarehouseLocation> locations = new ArrayList<>();
}
