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
@Table(name = "wh_warehouse")
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "manager_name", length = 50)
    private String managerName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;
}
