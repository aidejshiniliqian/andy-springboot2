package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockOutOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockOutOrderItemMapper extends BaseMapper<StockOutOrderItem> {

    @Select("SELECT i.*, m.name as material_name, m.code as material_code FROM wh_stock_out_order_item i " +
            "LEFT JOIN wh_material m ON i.material_id = m.id " +
            "WHERE i.order_id = #{orderId}")
    List<StockOutOrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM wh_stock_out_order_item i " +
            "INNER JOIN wh_stock_out_order o ON i.order_id = o.id " +
            "WHERE o.deleted = 0 AND o.status = 1 " +
            "AND i.material_id = #{materialId} " +
            "AND (#{startDate} IS NULL OR o.order_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR o.order_date <= #{endDate})")
    Integer getTotalQuantityByMaterialId(@Param("materialId") Long materialId,
                                          @Param("startDate") java.time.LocalDateTime startDate,
                                          @Param("endDate") java.time.LocalDateTime endDate);
}
