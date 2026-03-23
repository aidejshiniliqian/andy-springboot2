package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_material_category")
public class MaterialCategory extends BaseEntity {
    private String name;

    @TableField
    private String code;

    private String remark;

    private Integer status;

    private Integer sort;

    private Long parentId;

    @TableField(exist = false)
    private MaterialCategory parent;

    @TableField(exist = false)
    private List<MaterialCategory> children;
}
