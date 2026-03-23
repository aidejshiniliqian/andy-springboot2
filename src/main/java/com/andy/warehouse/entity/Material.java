package com.andy.warehouse.entity;

import com.andy.warehouse.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("wh_material")
public class Material extends BaseEntity {

    private String name;

    private String code;

    private String barcode;

    private String specification;

    private String model;

    private String unit;

    private BigDecimal price;

    private Integer safetyStock;

    private Integer maxStock;

    private String description;

    private Integer status;

    private Long categoryId;

    @TableField(exist = false)
    private MaterialCategory category;
}
