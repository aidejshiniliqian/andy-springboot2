package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.WarehouseLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarehouseLocationMapper extends BaseMapper<WarehouseLocation> {

    @Select("SELECT * FROM warehouse_location WHERE warehouse_id = #{warehouseId} AND is_deleted = false")
    List<WarehouseLocation> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Select("SELECT COUNT(*) > 0 FROM warehouse_location WHERE location_code = #{locationCode} AND warehouse_id = #{warehouseId} AND is_deleted = false")
    boolean existsByLocationCodeAndWarehouseId(@Param("locationCode") String locationCode, @Param("warehouseId") Long warehouseId);
}
