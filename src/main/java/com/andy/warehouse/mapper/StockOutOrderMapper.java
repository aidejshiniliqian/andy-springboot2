package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockOutOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StockOutOrderMapper extends BaseMapper<StockOutOrder> {

    @Select("SELECT * FROM wh_stock_out_order WHERE order_no = #{orderNo} AND deleted = 0")
    StockOutOrder findByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0 AND warehouse_id = #{warehouseId}")
    List<StockOutOrder> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0 AND (#{warehouseId} IS NULL OR warehouse_id = #{warehouseId})")
    IPage<StockOutOrder> findByWarehouseId(Page<StockOutOrder> page, @Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0 AND status = #{status}")
    List<StockOutOrder> findByStatus(@Param("status") Integer status);

    @Select("SELECT o.*, w.name as warehouse_name FROM wh_stock_out_order o LEFT JOIN wh_warehouse w ON o.warehouse_id = w.id WHERE o.id = #{id} AND o.deleted = 0")
    StockOutOrder findByIdWithWarehouse(@Param("id") Long id);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0 " +
            "AND (#{warehouseId} IS NULL OR warehouse_id = #{warehouseId}) " +
            "AND (#{startDate} IS NULL OR order_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR order_date <= #{endDate}) " +
            "AND (#{status} IS NULL OR status = #{status})")
    IPage<StockOutOrder> searchOrders(Page<StockOutOrder> page, 
                                       @Param("warehouseId") Long warehouseId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM wh_stock_out_order WHERE deleted = 0 AND DATE(order_date) = DATE(#{date})")
    Integer countByOrderDate(@Param("date") LocalDateTime date);

    @Select("SELECT COALESCE(SUM(i.quantity), 0) FROM wh_stock_out_order o " +
            "INNER JOIN wh_stock_out_order_item i ON o.id = i.order_id " +
            "WHERE o.deleted = 0 AND DATE(o.order_date) = DATE(#{date})")
    Integer sumQuantityByOrderDate(@Param("date") LocalDateTime date);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0 " +
            "AND (#{warehouseId} IS NULL OR warehouse_id = #{warehouseId}) " +
            "AND (#{startDate} IS NULL OR order_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR order_date <= #{endDate}) " +
            "AND (#{status} IS NULL OR status = #{status})")
    List<StockOutOrder> searchOrders(@Param("warehouseId") Long warehouseId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("status") Integer status);

    @Select("SELECT * FROM wh_stock_out_order WHERE deleted = 0")
    List<StockOutOrder> selectAll();

    @Select("SELECT i.*, m.name as material_name, m.code as material_code FROM wh_stock_out_order_item i " +
            "LEFT JOIN wh_material m ON i.material_id = m.id " +
            "WHERE i.order_id = #{orderId}")
    List<com.andy.warehouse.entity.StockOutOrderItem> findItemsByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COALESCE(SUM(i.quantity), 0) FROM wh_stock_out_order o " +
            "INNER JOIN wh_stock_out_order_item i ON o.id = i.order_id " +
            "WHERE o.deleted = 0 AND o.status = 1 " +
            "AND o.order_date >= #{startDate} AND o.order_date <= #{endDate}")
    Long getTotalQuantityByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
