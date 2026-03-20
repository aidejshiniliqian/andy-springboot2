package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"warehouse_id", "material_id"})
})
public class Inventory extends BaseEntity {
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id")
    private Material material;
}
