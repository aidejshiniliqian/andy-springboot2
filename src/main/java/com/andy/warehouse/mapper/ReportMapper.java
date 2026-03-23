package com.andy.warehouse.mapper;

import com.andy.warehouse.dto.report.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    interface InventorySummaryProjection {
        Long getWarehouseId();
        String getWarehouseName();
        Long getMaterialId();
        String getMaterialCode();
        String getMaterialName();
        String getCategoryName();
        String getUnit();
        BigDecimal getQuantity();
        BigDecimal getAvailableQuantity();
        BigDecimal getLockedQuantity();
        BigDecimal getUnitPrice();
        BigDecimal getTotalAmount();
        BigDecimal getSafetyStock();
    }

    interface StockInSummaryProjection {
        String getPeriod();
        Long getWarehouseId();
        String getWarehouseName();
        Long getMaterialId();
        String getMaterialCode();
        String getMaterialName();
        String getCategoryName();
        BigDecimal getStockInQuantity();
        BigDecimal getStockInAmount();
    }

    interface StockOutSummaryProjection {
        String getPeriod();
        Long getWarehouseId();
        String getWarehouseName();
        Long getMaterialId();
        String getMaterialCode();
        String getMaterialName();
        String getCategoryName();
        BigDecimal getStockOutQuantity();
        BigDecimal getStockOutAmount();
    }

    interface InventoryDetailProjection {
        String getRecordNo();
        String getRecordType();
        String getBizType();
        String getBizNo();
        LocalDateTime getRecordTime();
        Long getMaterialId();
        String getMaterialCode();
        String getMaterialName();
        Long getWarehouseId();
        String getWarehouseName();
        String getLocationCode();
        BigDecimal getQuantity();
        BigDecimal getBeforeQuantity();
        BigDecimal getAfterQuantity();
        String getUnit();
        String getBatchNo();
        String getOperatorName();
        String getRemark();
    }

    interface InventoryAgeProjection {
        Long getWarehouseId();
        String getWarehouseName();
        Long getMaterialId();
        String getMaterialCode();
        String getMaterialName();
        String getCategoryName();
        String getBatchNo();
        LocalDate getProductionDate();
        LocalDate getExpiryDate();
        BigDecimal getQuantity();
        String getUnit();
        Integer getAgeDays();
        BigDecimal getAmount();
    }

    interface WarehouseDistributionProjection {
        Long getWarehouseId();
        String getWarehouseName();
        BigDecimal getTotalQuantity();
        BigDecimal getTotalAmount();
    }

    interface TrendDataProjection {
        String getDate();
        BigDecimal getQuantity();
        BigDecimal getAmount();
    }

    interface CategoryDistributionProjection {
        String getCategoryName();
        BigDecimal getAmount();
    }

    @Select("<script>" +
            "SELECT " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "m.id as materialId, " +
            "m.material_code as materialCode, " +
            "m.material_name as materialName, " +
            "mc.category_name as categoryName, " +
            "m.unit as unit, " +
            "COALESCE(SUM(i.quantity), 0) as quantity, " +
            "COALESCE(SUM(i.available_quantity), 0) as availableQuantity, " +
            "COALESCE(SUM(i.locked_quantity), 0) as lockedQuantity, " +
            "m.purchase_price as unitPrice, " +
            "COALESCE(SUM(i.quantity * m.purchase_price), 0) as totalAmount, " +
            "m.safety_stock as safetyStock " +
            "FROM wms_inventory i " +
            "JOIN wms_material m ON i.material_id = m.id " +
            "LEFT JOIN wms_material_category mc ON m.category_id = mc.id " +
            "JOIN wms_warehouse w ON i.warehouse_id = w.id " +
            "WHERE i.is_deleted = false " +
            "<if test='warehouseId != null'> AND i.warehouse_id = #{warehouseId} </if> " +
            "<if test='materialId != null'> AND i.material_id = #{materialId} </if> " +
            "<if test='categoryId != null'> AND m.category_id = #{categoryId} </if> " +
            "<if test='materialCode != null'> AND m.material_code LIKE CONCAT('%', #{materialCode}, '%') </if> " +
            "<if test='materialName != null'> AND m.material_name LIKE CONCAT('%', #{materialName}, '%') </if> " +
            "GROUP BY w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name, m.unit, m.purchase_price, m.safety_stock " +
            "ORDER BY w.warehouse_name, m.material_code" +
            "</script>")
    IPage<InventorySummaryProjection> getInventorySummary(Page<InventorySummaryProjection> page,
                                                          @Param("warehouseId") Long warehouseId,
                                                          @Param("materialId") Long materialId,
                                                          @Param("categoryId") Long categoryId,
                                                          @Param("materialCode") String materialCode,
                                                          @Param("materialName") String materialName);

    @Select("<script>" +
            "SELECT " +
            "DATE_FORMAT(sio.order_date, '%Y-%m') as period, " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "m.id as materialId, " +
            "m.material_code as materialCode, " +
            "m.material_name as materialName, " +
            "mc.category_name as categoryName, " +
            "COALESCE(SUM(sii.quantity), 0) as stockInQuantity, " +
            "COALESCE(SUM(sii.total_amount), 0) as stockInAmount " +
            "FROM wms_stock_in_order sio " +
            "JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id " +
            "JOIN wms_material m ON sii.material_id = m.id " +
            "LEFT JOIN wms_material_category mc ON m.category_id = mc.id " +
            "JOIN wms_warehouse w ON sio.warehouse_id = w.id " +
            "WHERE sio.is_deleted = false " +
            "AND sio.status = 'COMPLETED' " +
            "<if test='warehouseId != null'> AND sio.warehouse_id = #{warehouseId} </if> " +
            "<if test='materialId != null'> AND sii.material_id = #{materialId} </if> " +
            "<if test='categoryId != null'> AND m.category_id = #{categoryId} </if> " +
            "<if test='startDate != null'> AND sio.order_date &gt;= #{startDate} </if> " +
            "<if test='endDate != null'> AND sio.order_date &lt;= #{endDate} </if> " +
            "GROUP BY DATE_FORMAT(sio.order_date, '%Y-%m'), w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name " +
            "ORDER BY period DESC, w.warehouse_name, m.material_code" +
            "</script>")
    IPage<StockInSummaryProjection> getStockInSummary(Page<StockInSummaryProjection> page,
                                                      @Param("warehouseId") Long warehouseId,
                                                      @Param("materialId") Long materialId,
                                                      @Param("categoryId") Long categoryId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Select("<script>" +
            "SELECT " +
            "DATE_FORMAT(soo.order_date, '%Y-%m') as period, " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "m.id as materialId, " +
            "m.material_code as materialCode, " +
            "m.material_name as materialName, " +
            "mc.category_name as categoryName, " +
            "COALESCE(SUM(soi.quantity), 0) as stockOutQuantity, " +
            "COALESCE(SUM(soi.total_amount), 0) as stockOutAmount " +
            "FROM wms_stock_out_order soo " +
            "JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id " +
            "JOIN wms_material m ON soi.material_id = m.id " +
            "LEFT JOIN wms_material_category mc ON m.category_id = mc.id " +
            "JOIN wms_warehouse w ON soo.warehouse_id = w.id " +
            "WHERE soo.is_deleted = false " +
            "AND soo.status = 'COMPLETED' " +
            "<if test='warehouseId != null'> AND soo.warehouse_id = #{warehouseId} </if> " +
            "<if test='materialId != null'> AND soi.material_id = #{materialId} </if> " +
            "<if test='categoryId != null'> AND m.category_id = #{categoryId} </if> " +
            "<if test='startDate != null'> AND soo.order_date &gt;= #{startDate} </if> " +
            "<if test='endDate != null'> AND soo.order_date &lt;= #{endDate} </if> " +
            "GROUP BY DATE_FORMAT(soo.order_date, '%Y-%m'), w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name " +
            "ORDER BY period DESC, w.warehouse_name, m.material_code" +
            "</script>")
    IPage<StockOutSummaryProjection> getStockOutSummary(Page<StockOutSummaryProjection> page,
                                                        @Param("warehouseId") Long warehouseId,
                                                        @Param("materialId") Long materialId,
                                                        @Param("categoryId") Long categoryId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Select("<script>" +
            "SELECT " +
            "ir.record_no as recordNo, " +
            "ir.record_type as recordType, " +
            "ir.biz_type as bizType, " +
            "ir.biz_no as bizNo, " +
            "ir.created_at as recordTime, " +
            "m.id as materialId, " +
            "m.material_code as materialCode, " +
            "m.material_name as materialName, " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "wl.location_code as locationCode, " +
            "ir.quantity as quantity, " +
            "ir.before_quantity as beforeQuantity, " +
            "ir.after_quantity as afterQuantity, " +
            "ir.unit as unit, " +
            "ir.batch_no as batchNo, " +
            "u.real_name as operatorName, " +
            "ir.remark as remark " +
            "FROM wms_inventory_record ir " +
            "JOIN wms_material m ON ir.material_id = m.id " +
            "JOIN wms_warehouse w ON ir.warehouse_id = w.id " +
            "LEFT JOIN wms_warehouse_location wl ON ir.location_id = wl.id " +
            "LEFT JOIN sys_user u ON ir.operator_id = u.id " +
            "WHERE ir.is_deleted = false " +
            "<if test='warehouseId != null'> AND ir.warehouse_id = #{warehouseId} </if> " +
            "<if test='materialId != null'> AND ir.material_id = #{materialId} </if> " +
            "<if test='startDate != null'> AND DATE(ir.created_at) &gt;= #{startDate} </if> " +
            "<if test='endDate != null'> AND DATE(ir.created_at) &lt;= #{endDate} </if> " +
            "<if test='recordType != null'> AND ir.record_type = #{recordType} </if> " +
            "ORDER BY ir.created_at DESC" +
            "</script>")
    IPage<InventoryDetailProjection> getInventoryDetail(Page<InventoryDetailProjection> page,
                                                        @Param("warehouseId") Long warehouseId,
                                                        @Param("materialId") Long materialId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate,
                                                        @Param("recordType") String recordType);

    @Select("<script>" +
            "SELECT " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "m.id as materialId, " +
            "m.material_code as materialCode, " +
            "m.material_name as materialName, " +
            "mc.category_name as categoryName, " +
            "i.batch_no as batchNo, " +
            "i.production_date as productionDate, " +
            "i.expiry_date as expiryDate, " +
            "i.quantity as quantity, " +
            "i.unit as unit, " +
            "DATEDIFF(CURDATE(), COALESCE(i.production_date, i.created_at)) as ageDays, " +
            "i.quantity * m.purchase_price as amount " +
            "FROM wms_inventory i " +
            "JOIN wms_material m ON i.material_id = m.id " +
            "LEFT JOIN wms_material_category mc ON m.category_id = mc.id " +
            "JOIN wms_warehouse w ON i.warehouse_id = w.id " +
            "WHERE i.is_deleted = false " +
            "AND i.quantity &gt; 0 " +
            "<if test='warehouseId != null'> AND i.warehouse_id = #{warehouseId} </if> " +
            "<if test='materialId != null'> AND i.material_id = #{materialId} </if> " +
            "<if test='categoryId != null'> AND m.category_id = #{categoryId} </if> " +
            "<if test='maxAge != null'> AND DATEDIFF(CURDATE(), COALESCE(i.production_date, i.created_at)) &lt;= #{maxAge} </if> " +
            "ORDER BY ageDays DESC, w.warehouse_name, m.material_code" +
            "</script>")
    IPage<InventoryAgeProjection> getInventoryAge(Page<InventoryAgeProjection> page,
                                                  @Param("warehouseId") Long warehouseId,
                                                  @Param("materialId") Long materialId,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("maxAge") Integer maxAge);

    @Select("SELECT " +
            "w.id as warehouseId, " +
            "w.warehouse_name as warehouseName, " +
            "SUM(i.quantity) as totalQuantity, " +
            "SUM(i.quantity * COALESCE(m.purchase_price, 0)) as totalAmount " +
            "FROM wms_warehouse w " +
            "LEFT JOIN wms_inventory i ON w.id = i.warehouse_id AND i.is_deleted = false " +
            "LEFT JOIN wms_material m ON i.material_id = m.id " +
            "WHERE w.is_deleted = false " +
            "AND w.status = 1 " +
            "GROUP BY w.id, w.warehouse_name " +
            "ORDER BY totalAmount DESC")
    List<WarehouseDistributionProjection> getWarehouseDistribution();

    @Select("SELECT " +
            "DATE_FORMAT(sio.order_date, '%Y-%m-%d') as date, " +
            "SUM(sii.quantity) as quantity, " +
            "SUM(sii.total_amount) as amount " +
            "FROM wms_stock_in_order sio " +
            "JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id " +
            "WHERE sio.is_deleted = false " +
            "AND sio.status = 'COMPLETED' " +
            "AND sio.order_date &gt;= #{startDate} " +
            "AND sio.order_date &lt;= #{endDate} " +
            "GROUP BY DATE_FORMAT(sio.order_date, '%Y-%m-%d') " +
            "ORDER BY date")
    List<TrendDataProjection> getStockInTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT " +
            "DATE_FORMAT(soo.order_date, '%Y-%m-%d') as date, " +
            "SUM(soi.quantity) as quantity, " +
            "SUM(soi.total_amount) as amount " +
            "FROM wms_stock_out_order soo " +
            "JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id " +
            "WHERE soo.is_deleted = false " +
            "AND soo.status = 'COMPLETED' " +
            "AND soo.order_date &gt;= #{startDate} " +
            "AND soo.order_date &lt;= #{endDate} " +
            "GROUP BY DATE_FORMAT(soo.order_date, '%Y-%m-%d') " +
            "ORDER BY date")
    List<TrendDataProjection> getStockOutTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT COALESCE(SUM(i.quantity), 0) FROM wms_inventory i WHERE i.is_deleted = false")
    BigDecimal getTotalInventoryQuantity();

    @Select("SELECT COALESCE(SUM(i.quantity * m.purchase_price), 0) FROM wms_inventory i " +
            "JOIN wms_material m ON i.material_id = m.id WHERE i.is_deleted = false")
    BigDecimal getTotalInventoryAmount();

    @Select("SELECT COUNT(DISTINCT material_id) FROM wms_inventory WHERE quantity &lt; (SELECT safety_stock FROM wms_material WHERE id = material_id) AND is_deleted = false")
    Long getLowStockMaterialCount();

    @Select("SELECT COUNT(*) FROM wms_stock_in_order WHERE status = 'PENDING' AND is_deleted = false")
    Long getPendingStockInCount();

    @Select("SELECT COUNT(*) FROM wms_stock_out_order WHERE status = 'PENDING' AND is_deleted = false")
    Long getPendingStockOutCount();

    @Select("SELECT COUNT(*) FROM wms_stock_in_order WHERE status = 'COMPLETED' AND DATE(order_date) = CURDATE() AND is_deleted = false")
    Long getTodayStockInCount();

    @Select("SELECT COUNT(*) FROM wms_stock_out_order WHERE status = 'COMPLETED' AND DATE(order_date) = CURDATE() AND is_deleted = false")
    Long getTodayStockOutCount();

    @Select("SELECT COALESCE(SUM(sii.quantity), 0) FROM wms_stock_in_order sio " +
            "JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id " +
            "WHERE sio.status = 'COMPLETED' AND DATE(sio.order_date) = CURDATE() AND sio.is_deleted = false")
    BigDecimal getTodayStockInQuantity();

    @Select("SELECT COALESCE(SUM(soi.quantity), 0) FROM wms_stock_out_order soo " +
            "JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id " +
            "WHERE soo.status = 'COMPLETED' AND DATE(soo.order_date) = CURDATE() AND soo.is_deleted = false")
    BigDecimal getTodayStockOutQuantity();

    @Select("SELECT " +
            "mc.category_name as categoryName, " +
            "COALESCE(SUM(i.quantity * m.purchase_price), 0) as amount " +
            "FROM wms_material_category mc " +
            "LEFT JOIN wms_material m ON mc.id = m.category_id " +
            "LEFT JOIN wms_inventory i ON m.id = i.material_id AND i.is_deleted = false " +
            "WHERE mc.is_deleted = false " +
            "GROUP BY mc.id, mc.category_name " +
            "ORDER BY amount DESC")
    List<CategoryDistributionProjection> getCategoryDistribution();
}
