package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface StockInOrderMapper extends BaseMapper<StockInOrder> {

    @Select("SELECT * FROM stock_in_order WHERE order_no = #{orderNo} AND is_deleted = false")
    Optional<StockInOrder> findByOrderNo(@Param("orderNo") String orderNo);

    @Select("<script>" +
            "SELECT sio.*, w.warehouse_name, u.real_name as operator_name " +
            "FROM stock_in_order sio " +
            "LEFT JOIN warehouse w ON sio.warehouse_id = w.id " +
            "LEFT JOIN sys_user u ON sio.operator_id = u.id " +
            "WHERE sio.is_deleted = false " +
            "<if test='orderNo != null'> AND sio.order_no LIKE CONCAT('%', #{orderNo}, '%') </if> " +
            "<if test='orderType != null'> AND sio.order_type = #{orderType} </if> " +
            "<if test='warehouseId != null'> AND sio.warehouse_id = #{warehouseId} </if> " +
            "<if test='status != null'> AND sio.status = #{status} </if> " +
            "<if test='startDate != null'> AND sio.order_date &gt;= #{startDate} </if> " +
            "<if test='endDate != null'> AND sio.order_date &lt;= #{endDate} </if> " +
            "ORDER BY sio.created_at DESC" +
            "</script>")
    IPage<StockInOrder> findByConditions(Page<StockInOrder> page,
                                          @Param("orderNo") String orderNo,
                                          @Param("orderType") String orderType,
                                          @Param("warehouseId") Long warehouseId,
                                          @Param("status") String status,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
