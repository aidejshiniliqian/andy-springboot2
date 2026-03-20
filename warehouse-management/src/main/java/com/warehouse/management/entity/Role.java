package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(length = 200)
    private String description;

    private Integer status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}
