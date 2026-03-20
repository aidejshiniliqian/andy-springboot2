package com.warehouse.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "biz_material_category")
public class MaterialCategory extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(length = 500)
    private String remark;

    private Integer status;

    private Integer sort;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private MaterialCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<MaterialCategory> children;
}
