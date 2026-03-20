package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_permission")
@Getter
@Setter
public class Permission extends BaseEntity {

    @Column(name = "permission_code", unique = true, nullable = false, length = 50)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "resource_url", length = 200)
    private String resourceUrl;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Permission parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permission> children = new ArrayList<>();

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "status")
    private Integer status = 1;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();
}
