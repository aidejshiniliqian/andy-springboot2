package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wh_material")
public class Material extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "safety_stock")
    private Integer safetyStock;

    @Column(name = "max_stock")
    private Integer maxStock;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MaterialCategory category;
}
