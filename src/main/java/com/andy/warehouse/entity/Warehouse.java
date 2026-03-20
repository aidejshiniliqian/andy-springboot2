package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wms_warehouse")
@Getter
@Setter
public class Warehouse extends BaseEntity {

    @Column(name = "warehouse_code", unique = true, nullable = false, length = 50)
    private String warehouseCode;

    @Column(name = "warehouse_name", nullable = false, length = 100)
    private String warehouseName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "capacity", precision = 15, scale = 2)
    private BigDecimal capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WarehouseZone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();
}
