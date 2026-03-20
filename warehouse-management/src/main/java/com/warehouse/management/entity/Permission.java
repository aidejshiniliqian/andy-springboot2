package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String code;

    @Column(length = 200)
    private String url;

    @Column(length = 50)
    private String method;

    private Integer type;

    private Integer sort;

    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Permission parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Permission> children;
}
