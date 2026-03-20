package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_out_stock")
public class OutStock extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String orderNo;

    @Column(length = 100)
    private String receiver;

    @Column(length = 11)
    private String receiverPhone;

    @Column(length = 200)
    private String receiverAddress;

    private Integer type;

    private LocalDateTime outStockTime;

    @Column(length = 500)
    private String remark;

    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operator_id")
    private User operator;

    @OneToMany(mappedBy = "outStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutStockDetail> details;
}
