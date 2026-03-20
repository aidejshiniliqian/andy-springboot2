package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_material")
public class Material extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(length = 50)
    private String spec;

    @Column(length = 50)
    private String unit;

    private BigDecimal price;

    @Column(length = 500)
    private String remark;

    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private MaterialCategory category;
}
