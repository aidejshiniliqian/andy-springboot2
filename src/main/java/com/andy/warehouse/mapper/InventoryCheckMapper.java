package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.InventoryCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    @Select("SELECT * FROM wh_inventory_check WHERE check_no = #{checkNo} AND deleted = 0")
    InventoryCheck findByCheckNo(@Param("checkNo") String checkNo);

    @Select("SELECT * FROM wh_inventory_check WHERE deleted = 0 AND warehouse_id = #{warehouseId}")
    List<InventoryCheck> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM wh_inventory_check WHERE deleted = 0 " +
            "AND (#{warehouseId} IS NULL OR warehouse_id = #{warehouseId}) " +
            "AND (#{startDate} IS NULL OR check_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR check_date <= #{endDate}) " +
            "AND (#{status} IS NULL OR status = #{status})")
    IPage<InventoryCheck> searchChecks(Page<InventoryCheck> page,
                                        @Param("warehouseId") Long warehouseId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("status") Integer status);

    @Select("SELECT ic.*, w.name as warehouse_name FROM wh_inventory_check ic LEFT JOIN wh_warehouse w ON ic.warehouse_id = w.id WHERE ic.id = #{id} AND ic.deleted = 0")
    InventoryCheck findByIdWithWarehouse(@Param("id") Long id);

    @Select("SELECT * FROM wh_inventory_check WHERE deleted = 0 AND status = 1 ORDER BY check_date DESC")
    List<InventoryCheck> findCompletedChecks();
}
