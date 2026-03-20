package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_in_stock")
public class InStock extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String orderNo;

    @Column(length = 100)
    private String supplier;

    @Column(length = 50)
    private String supplierPhone;

    @Column(length = 200)
    private String supplierAddress;

    private Integer type;

    private LocalDateTime inStockTime;

    @Column(length = 500)
    private String remark;

    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operator_id")
    private User operator;

    @OneToMany(mappedBy = "inStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InStockDetail> details;
}
