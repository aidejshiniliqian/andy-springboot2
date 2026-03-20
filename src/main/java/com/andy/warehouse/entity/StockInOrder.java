package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wms_stock_in_order")
@Getter
@Setter
public class StockInOrder extends BaseEntity {

    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;

    @Column(name = "order_type", length = 20)
    private String orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "supplier_contact", length = 50)
    private String supplierContact;

    @Column(name = "supplier_phone", length = 20)
    private String supplierPhone;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_quantity", precision = 15, scale = 2)
    private BigDecimal totalQuantity;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Column(name = "actual_date")
    private LocalDateTime actualDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "stockInOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockInItem> items = new ArrayList<>();
}
