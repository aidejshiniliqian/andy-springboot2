package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("<script>" +
            "SELECT i.*, m.material_code, m.material_name, m.specification, " +
            "w.warehouse_name, l.location_code, l.location_name " +
            "FROM inventory i " +
            "LEFT JOIN material m ON i.material_id = m.id " +
            "LEFT JOIN warehouse w ON i.warehouse_id = w.id " +
            "LEFT JOIN warehouse_location l ON i.location_id = l.id " +
            "WHERE i.is_deleted = false " +
            "<if test='materialId != null'> AND i.material_id = #{materialId} </if> " +
            "<if test='warehouseId != null'> AND i.warehouse_id = #{warehouseId} </if> " +
            "<if test='locationId != null'> AND i.location_id = #{locationId} </if> " +
            "<if test='batchNo != null'> AND i.batch_no LIKE CONCAT('%', #{batchNo}, '%') </if> " +
            "ORDER BY i.created_at DESC" +
            "</script>")
    IPage<Inventory> findByConditions(Page<Inventory> page,
                                       @Param("materialId") Long materialId,
                                       @Param("warehouseId") Long warehouseId,
                                       @Param("locationId") Long locationId,
                                       @Param("batchNo") String batchNo);

    @Select("SELECT * FROM inventory WHERE material_id = #{materialId} AND is_deleted = false")
    List<Inventory> findByMaterialId(@Param("materialId") Long materialId);

    @Select("SELECT * FROM inventory WHERE warehouse_id = #{warehouseId} AND status = #{status} AND is_deleted = false")
    List<Inventory> findByWarehouseIdAndStatus(@Param("warehouseId") Long warehouseId, @Param("status") Integer status);

    @Select("SELECT * FROM inventory WHERE material_id = #{materialId} AND warehouse_id = #{warehouseId} AND is_deleted = false")
    List<Inventory> findByMaterialIdAndWarehouseId(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM inventory WHERE material_id = #{materialId} AND warehouse_id = #{warehouseId} " +
            "AND location_id = #{locationId} AND batch_no = #{batchNo} AND is_deleted = false")
    Optional<Inventory> findByMaterialIdAndWarehouseIdAndLocationIdAndBatchNo(
            @Param("materialId") Long materialId,
            @Param("warehouseId") Long warehouseId,
            @Param("locationId") Long locationId,
            @Param("batchNo") String batchNo);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory WHERE material_id = #{materialId} AND is_deleted = false")
    BigDecimal getTotalQuantityByMaterialId(@Param("materialId") Long materialId);

    @Update("UPDATE inventory SET quantity = quantity + #{quantity}, available_quantity = available_quantity + #{quantity} " +
            "WHERE id = #{id}")
    void addQuantity(@Param("id") Long id, @Param("quantity") BigDecimal quantity);

    @Update("UPDATE inventory SET quantity = quantity - #{quantity}, available_quantity = available_quantity - #{quantity} " +
            "WHERE id = #{id}")
    void deductQuantity(@Param("id") Long id, @Param("quantity") BigDecimal quantity);
}
