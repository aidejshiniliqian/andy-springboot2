package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockInItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockInItemMapper extends BaseMapper<StockInItem> {

    @Select("SELECT sii.*, m.material_code, m.material_name, m.specification, " +
            "l.location_code, l.location_name " +
            "FROM stock_in_item sii " +
            "LEFT JOIN material m ON sii.material_id = m.id " +
            "LEFT JOIN warehouse_location l ON sii.location_id = l.id " +
            "WHERE sii.stock_in_order_id = #{orderId} AND sii.is_deleted = false")
    List<StockInItem> findByStockInOrderId(@Param("orderId") Long orderId);
}
