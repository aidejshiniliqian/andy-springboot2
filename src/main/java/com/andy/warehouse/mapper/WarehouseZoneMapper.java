package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.WarehouseZone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface WarehouseZoneMapper extends BaseMapper<WarehouseZone> {

    @Select("SELECT * FROM wms_warehouse_zone WHERE zone_code = #{zoneCode} AND warehouse_id = #{warehouseId} AND is_deleted = false")
    Optional<WarehouseZone> findByZoneCodeAndWarehouseId(@Param("zoneCode") String zoneCode, @Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM wms_warehouse_zone WHERE warehouse_id = #{warehouseId} AND status = #{status} AND is_deleted = false")
    List<WarehouseZone> findByWarehouseIdAndStatus(@Param("warehouseId") Long warehouseId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) > 0 FROM wms_warehouse_zone WHERE zone_code = #{zoneCode} AND warehouse_id = #{warehouseId} AND is_deleted = false")
    boolean existsByZoneCodeAndWarehouseId(@Param("zoneCode") String zoneCode, @Param("warehouseId") Long warehouseId);
}
