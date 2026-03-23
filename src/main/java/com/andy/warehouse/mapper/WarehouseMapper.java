package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Warehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    @Select("SELECT * FROM warehouse WHERE warehouse_code = #{warehouseCode} AND is_deleted = false")
    Optional<Warehouse> findByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    @Select("SELECT COUNT(*) > 0 FROM warehouse WHERE warehouse_code = #{warehouseCode} AND is_deleted = false")
    boolean existsByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    @Select("<script>" +
            "SELECT * FROM warehouse " +
            "WHERE is_deleted = false " +
            "<if test='warehouseCode != null'> AND warehouse_code LIKE CONCAT('%', #{warehouseCode}, '%') </if> " +
            "<if test='warehouseName != null'> AND warehouse_name LIKE CONCAT('%', #{warehouseName}, '%') </if> " +
            "<if test='status != null'> AND status = #{status} </if> " +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<Warehouse> findByConditions(Page<Warehouse> page,
                                       @Param("warehouseCode") String warehouseCode,
                                       @Param("warehouseName") String warehouseName,
                                       @Param("status") Integer status);
}
