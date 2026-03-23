package com.warehouse.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_material")
public class Material extends BaseEntity {
    @TableField
    private String name;

    @TableField
    private String code;

    @TableField
    private String spec;

    @TableField
    private String unit;

    private BigDecimal price;

    @TableField
    private String remark;

    private Integer status;

    @TableField(exist = false)
    
    private MaterialCategory category;
}
