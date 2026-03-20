package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_organization")
public class Organization extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String contact;

    @Column(length = 11)
    private String phone;

    private Integer sort;

    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Organization> children;
}
