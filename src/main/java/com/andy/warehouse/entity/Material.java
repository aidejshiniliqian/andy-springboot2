package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wms_material")
@Getter
@Setter
public class Material extends BaseEntity {

    @Column(name = "material_code", unique = true, nullable = false, length = 50)
    private String materialCode;

    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "model", length = 50)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MaterialCategory category;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "barcode", length = 50)
    private String barcode;

    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "safety_stock", precision = 15, scale = 2)
    private BigDecimal safetyStock;

    @Column(name = "max_stock", precision = 15, scale = 2)
    private BigDecimal maxStock;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();
}
