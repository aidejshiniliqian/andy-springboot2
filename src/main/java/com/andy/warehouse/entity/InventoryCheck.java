package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wh_inventory_check")
public class InventoryCheck extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_no", unique = true, length = 50)
    private String checkNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "check_date")
    private LocalDateTime checkDate;

    @Column(name = "status", nullable = false)
    private Integer status = 0;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "variance_items")
    private Integer varianceItems;

    @Column(name = "total_variance_amount", precision = 12, scale = 2)
    private BigDecimal totalVarianceAmount;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operator_name", length = 50)
    private String operatorName;

    @OneToMany(mappedBy = "inventoryCheck", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InventoryCheckItem> items = new ArrayList<>();
}
