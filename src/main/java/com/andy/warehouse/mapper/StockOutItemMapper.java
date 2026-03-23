package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockOutItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockOutItemMapper extends BaseMapper<StockOutItem> {

    @Select("SELECT soi.*, m.material_code, m.material_name, m.specification, " +
            "l.location_code, l.location_name " +
            "FROM stock_out_item soi " +
            "LEFT JOIN material m ON soi.material_id = m.id " +
            "LEFT JOIN warehouse_location l ON soi.location_id = l.id " +
            "WHERE soi.stock_out_order_id = #{orderId} AND soi.is_deleted = false")
    List<StockOutItem> findByStockOutOrderId(@Param("orderId") Long orderId);
}
