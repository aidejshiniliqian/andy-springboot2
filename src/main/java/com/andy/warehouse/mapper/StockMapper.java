package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    @Select("SELECT * FROM wh_stock WHERE warehouse_id = #{warehouseId} AND material_id = #{materialId}")
    Stock findByWarehouseAndMaterial(@Param("warehouseId") Long warehouseId, @Param("materialId") Long materialId);

    @Select("SELECT * FROM wh_stock WHERE warehouse_id = #{warehouseId}")
    List<Stock> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Select("SELECT * FROM wh_stock WHERE material_id = #{materialId}")
    List<Stock> findByMaterialId(@Param("materialId") Long materialId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM wh_stock WHERE material_id = #{materialId}")
    Integer getTotalQuantityByMaterialId(@Param("materialId") Long materialId);

    @Update("UPDATE wh_stock SET quantity = quantity + #{quantity}, available_quantity = available_quantity + #{quantity} WHERE id = #{id}")
    void addQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE wh_stock SET quantity = quantity - #{quantity}, available_quantity = available_quantity - #{quantity} WHERE id = #{id} AND quantity >= #{quantity}")
    int subtractQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE wh_stock SET locked_quantity = locked_quantity + #{quantity}, available_quantity = available_quantity - #{quantity} WHERE id = #{id} AND available_quantity >= #{quantity}")
    int lockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE wh_stock SET locked_quantity = locked_quantity - #{quantity}, available_quantity = available_quantity + #{quantity} WHERE id = #{id} AND locked_quantity >= #{quantity}")
    int unlockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Select("SELECT s.*, m.name as material_name, m.code as material_code, mc.name as category_name FROM wh_stock s " +
            "LEFT JOIN wh_material m ON s.material_id = m.id " +
            "LEFT JOIN wh_material_category mc ON m.category_id = mc.id " +
            "WHERE s.quantity > 0")
    List<Stock> findAllWithMaterialAndCategory();

    @Select("SELECT s.*, m.name as material_name, m.code as material_code FROM wh_stock s " +
            "LEFT JOIN wh_material m ON s.material_id = m.id " +
            "WHERE s.warehouse_id = #{warehouseId} AND s.quantity > 0")
    List<Stock> findByWarehouseIdWithMaterial(@Param("warehouseId") Long warehouseId);
}
