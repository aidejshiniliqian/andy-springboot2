package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@TableName("wms_material_category")
@Getter
@Setter
public class MaterialCategory extends BaseEntity {

    @TableField("category_code")
    private String categoryCode;

    @TableField("category_name")
    private String categoryName;

    @TableField("description")
    private String description;

    @TableField("parent_id")
    private Long parentId;

    @TableField("sort_order")
    private Integer sortOrder = 0;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private MaterialCategory parent;

    @TableField(exist = false)
    private List<MaterialCategory> children = new ArrayList<>();

    @TableField(exist = false)
    private List<Material> materials = new ArrayList<>();
}
