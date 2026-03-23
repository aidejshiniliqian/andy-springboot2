package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.InventoryRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InventoryRecordMapper extends BaseMapper<InventoryRecord> {

    @Select("<script>" +
            "SELECT ir.*, m.material_code, m.material_name, " +
            "w.warehouse_name, l.location_code, l.location_name, u.real_name as operator_name " +
            "FROM inventory_record ir " +
            "LEFT JOIN material m ON ir.material_id = m.id " +
            "LEFT JOIN warehouse w ON ir.warehouse_id = w.id " +
            "LEFT JOIN warehouse_location l ON ir.location_id = l.id " +
            "LEFT JOIN sys_user u ON ir.operator_id = u.id " +
            "WHERE ir.is_deleted = false " +
            "<if test='recordNo != null'> AND ir.record_no LIKE CONCAT('%', #{recordNo}, '%') </if> " +
            "<if test='recordType != null'> AND ir.record_type = #{recordType} </if> " +
            "<if test='materialId != null'> AND ir.material_id = #{materialId} </if> " +
            "<if test='warehouseId != null'> AND ir.warehouse_id = #{warehouseId} </if> " +
            "<if test='startTime != null'> AND ir.created_at &gt;= #{startTime} </if> " +
            "<if test='endTime != null'> AND ir.created_at &lt;= #{endTime} </if> " +
            "ORDER BY ir.created_at DESC" +
            "</script>")
    IPage<InventoryRecord> findByConditions(Page<InventoryRecord> page,
                                             @Param("recordNo") String recordNo,
                                             @Param("recordType") String recordType,
                                             @Param("materialId") Long materialId,
                                             @Param("warehouseId") Long warehouseId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    @Select("SELECT ir.*, m.material_code, m.material_name, w.warehouse_name " +
            "FROM inventory_record ir " +
            "LEFT JOIN material m ON ir.material_id = m.id " +
            "LEFT JOIN warehouse w ON ir.warehouse_id = w.id " +
            "WHERE ir.material_id = #{materialId} AND ir.warehouse_id = #{warehouseId} AND ir.is_deleted = false " +
            "ORDER BY ir.created_at DESC")
    List<InventoryRecord> findByMaterialAndWarehouse(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);
}
