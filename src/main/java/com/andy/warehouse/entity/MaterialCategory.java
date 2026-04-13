package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("wh_material_category")
public class MaterialCategory extends BaseEntity {

    private String name;

    private String code;

    private String description;

    private Integer status;

    private Long parentId;

    @TableField(exist = false)
    private MaterialCategory parent;
}
