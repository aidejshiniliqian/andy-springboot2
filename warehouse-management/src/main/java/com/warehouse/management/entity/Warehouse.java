package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_warehouse")
public class Warehouse extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(length = 200)
    private String address;

    @Column(length = 50)
    private String manager;

    @Column(length = 11)
    private String phone;

    @Column(length = 500)
    private String remark;

    private Integer status;

    private Integer sort;
}
