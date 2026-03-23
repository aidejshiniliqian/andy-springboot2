package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.StockOutOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface StockOutOrderMapper extends BaseMapper<StockOutOrder> {

    @Select("SELECT * FROM stock_out_order WHERE order_no = #{orderNo} AND is_deleted = false")
    Optional<StockOutOrder> findByOrderNo(@Param("orderNo") String orderNo);

    @Select("<script>" +
            "SELECT soo.*, w.warehouse_name, u.real_name as operator_name " +
            "FROM stock_out_order soo " +
            "LEFT JOIN warehouse w ON soo.warehouse_id = w.id " +
            "LEFT JOIN sys_user u ON soo.operator_id = u.id " +
            "WHERE soo.is_deleted = false " +
            "<if test='orderNo != null'> AND soo.order_no LIKE CONCAT('%', #{orderNo}, '%') </if> " +
            "<if test='orderType != null'> AND soo.order_type = #{orderType} </if> " +
            "<if test='warehouseId != null'> AND soo.warehouse_id = #{warehouseId} </if> " +
            "<if test='status != null'> AND soo.status = #{status} </if> " +
            "<if test='startDate != null'> AND soo.order_date &gt;= #{startDate} </if> " +
            "<if test='endDate != null'> AND soo.order_date &lt;= #{endDate} </if> " +
            "ORDER BY soo.created_at DESC" +
            "</script>")
    IPage<StockOutOrder> findByConditions(Page<StockOutOrder> page,
                                           @Param("orderNo") String orderNo,
                                           @Param("orderType") String orderType,
                                           @Param("warehouseId") Long warehouseId,
                                           @Param("status") String status,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}
