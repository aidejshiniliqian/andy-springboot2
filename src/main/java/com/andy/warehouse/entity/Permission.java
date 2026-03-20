package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "code", unique = true, length = 100)
    private String code;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
