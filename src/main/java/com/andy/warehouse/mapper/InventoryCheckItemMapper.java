package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.InventoryCheckItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryCheckItemMapper extends BaseMapper<InventoryCheckItem> {

    @Select("SELECT i.*, m.name as material_name, m.code as material_code FROM wh_inventory_check_item i " +
            "LEFT JOIN wh_material m ON i.material_id = m.id " +
            "WHERE i.check_id = #{checkId}")
    List<InventoryCheckItem> findByCheckId(@Param("checkId") Long checkId);

    @Select("SELECT i.*, m.name as material_name, m.code as material_code FROM wh_inventory_check_item i " +
            "LEFT JOIN wh_material m ON i.material_id = m.id " +
            "WHERE i.check_id = #{checkId} AND i.variance_quantity != 0")
    List<InventoryCheckItem> findVarianceItemsByCheckId(@Param("checkId") Long checkId);
}
