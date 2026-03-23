package com.andy.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TableName("wms_material")
@Getter
@Setter
public class Material extends BaseEntity {

    @TableField("material_code")
    private String materialCode;

    @TableField("material_name")
    private String materialName;

    @TableField("specification")
    private String specification;

    @TableField("model")
    private String model;

    @TableField("category_id")
    private Long categoryId;

    @TableField("unit")
    private String unit;

    @TableField("barcode")
    private String barcode;

    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    @TableField("sale_price")
    private BigDecimal salePrice;

    @TableField("safety_stock")
    private BigDecimal safetyStock;

    @TableField("max_stock")
    private BigDecimal maxStock;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status = 1;

    @TableField(exist = false)
    private MaterialCategory category;

    @TableField(exist = false)
    private List<Inventory> inventories = new ArrayList<>();
}
