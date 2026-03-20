package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_out_stock_detail")
public class OutStockDetail extends BaseEntity {
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    @Column(length = 500)
    private String remark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "out_stock_id")
    private OutStock outStock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id")
    private Material material;
}
