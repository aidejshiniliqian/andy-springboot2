package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockInOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockInOrderItemMapper extends BaseMapper<StockInOrderItem> {

    @Select("SELECT i.*, m.name as material_name, m.code as material_code FROM wh_stock_in_order_item i " +
            "LEFT JOIN wh_material m ON i.material_id = m.id " +
            "WHERE i.order_id = #{orderId}")
    List<StockInOrderItem> findByOrderId(@Param("orderId") Long orderId);
}
