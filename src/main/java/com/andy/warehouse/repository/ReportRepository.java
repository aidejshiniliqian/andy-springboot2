package com.andy.warehouse.repository;

import com.andy.warehouse.dto.report.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<com.andy.warehouse.entity.Inventory, Long> {

    @Query(value = """
        SELECT 
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            m.id as materialId,
            m.material_code as materialCode,
            m.material_name as materialName,
            mc.category_name as categoryName,
            m.unit as unit,
            COALESCE(SUM(i.quantity), 0) as quantity,
            COALESCE(SUM(i.available_quantity), 0) as availableQuantity,
            COALESCE(SUM(i.locked_quantity), 0) as lockedQuantity,
            m.purchase_price as unitPrice,
            COALESCE(SUM(i.quantity * m.purchase_price), 0) as totalAmount,
            m.safety_stock as safetyStock
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        LEFT JOIN wms_material_category mc ON m.category_id = mc.id
        JOIN wms_warehouse w ON i.warehouse_id = w.id
        WHERE i.is_deleted = false
        AND (:warehouseId IS NULL OR i.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR i.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:materialCode IS NULL OR m.material_code LIKE CONCAT('%', :materialCode, '%'))
        AND (:materialName IS NULL OR m.material_name LIKE CONCAT('%', :materialName, '%'))
        GROUP BY w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name, m.unit, m.purchase_price, m.safety_stock
        ORDER BY w.warehouse_name, m.material_code
        """,
        countQuery = """
        SELECT COUNT(DISTINCT CONCAT(i.warehouse_id, '-', i.material_id))
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        WHERE i.is_deleted = false
        AND (:warehouseId IS NULL OR i.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR i.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:materialCode IS NULL OR m.material_code LIKE CONCAT('%', :materialCode, '%'))
        AND (:materialName IS NULL OR m.material_name LIKE CONCAT('%', :materialName, '%'))
        """,
        nativeQuery = true)
    Page<InventorySummaryProjection> getInventorySummary(
            @Param("warehouseId") Long warehouseId,
            @Param("materialId") Long materialId,
            @Param("categoryId") Long categoryId,
            @Param("materialCode") String materialCode,
            @Param("materialName") String materialName,
            Pageable pageable);

    @Query(value = """
        SELECT 
            DATE_FORMAT(sio.order_date, '%Y-%m') as period,
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            m.id as materialId,
            m.material_code as materialCode,
            m.material_name as materialName,
            mc.category_name as categoryName,
            COALESCE(SUM(sii.quantity), 0) as stockInQuantity,
            COALESCE(SUM(sii.total_amount), 0) as stockInAmount
        FROM wms_stock_in_order sio
        JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id
        JOIN wms_material m ON sii.material_id = m.id
        LEFT JOIN wms_material_category mc ON m.category_id = mc.id
        JOIN wms_warehouse w ON sio.warehouse_id = w.id
        WHERE sio.is_deleted = false
        AND sio.status = 'COMPLETED'
        AND (:warehouseId IS NULL OR sio.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR sii.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:startDate IS NULL OR sio.order_date >= :startDate)
        AND (:endDate IS NULL OR sio.order_date <= :endDate)
        GROUP BY DATE_FORMAT(sio.order_date, '%Y-%m'), w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name
        ORDER BY period DESC, w.warehouse_name, m.material_code
        """,
        countQuery = """
        SELECT COUNT(DISTINCT CONCAT(DATE_FORMAT(sio.order_date, '%Y-%m'), '-', sio.warehouse_id, '-', sii.material_id))
        FROM wms_stock_in_order sio
        JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id
        JOIN wms_material m ON sii.material_id = m.id
        WHERE sio.is_deleted = false
        AND sio.status = 'COMPLETED'
        AND (:warehouseId IS NULL OR sio.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR sii.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:startDate IS NULL OR sio.order_date >= :startDate)
        AND (:endDate IS NULL OR sio.order_date <= :endDate)
        """,
        nativeQuery = true)
    Page<StockInSummaryProjection> getStockInSummary(
            @Param("warehouseId") Long warehouseId,
            @Param("materialId") Long materialId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query(value = """
        SELECT 
            DATE_FORMAT(soo.order_date, '%Y-%m') as period,
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            m.id as materialId,
            m.material_code as materialCode,
            m.material_name as materialName,
            mc.category_name as categoryName,
            COALESCE(SUM(soi.quantity), 0) as stockOutQuantity,
            COALESCE(SUM(soi.total_amount), 0) as stockOutAmount
        FROM wms_stock_out_order soo
        JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id
        JOIN wms_material m ON soi.material_id = m.id
        LEFT JOIN wms_material_category mc ON m.category_id = mc.id
        JOIN wms_warehouse w ON soo.warehouse_id = w.id
        WHERE soo.is_deleted = false
        AND soo.status = 'COMPLETED'
        AND (:warehouseId IS NULL OR soo.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR soi.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:startDate IS NULL OR soo.order_date >= :startDate)
        AND (:endDate IS NULL OR soo.order_date <= :endDate)
        GROUP BY DATE_FORMAT(soo.order_date, '%Y-%m'), w.id, w.warehouse_name, m.id, m.material_code, m.material_name, mc.category_name
        ORDER BY period DESC, w.warehouse_name, m.material_code
        """,
        countQuery = """
        SELECT COUNT(DISTINCT CONCAT(DATE_FORMAT(soo.order_date, '%Y-%m'), '-', soo.warehouse_id, '-', soi.material_id))
        FROM wms_stock_out_order soo
        JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id
        JOIN wms_material m ON soi.material_id = m.id
        WHERE soo.is_deleted = false
        AND soo.status = 'COMPLETED'
        AND (:warehouseId IS NULL OR soo.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR soi.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:startDate IS NULL OR soo.order_date >= :startDate)
        AND (:endDate IS NULL OR soo.order_date <= :endDate)
        """,
        nativeQuery = true)
    Page<StockOutSummaryProjection> getStockOutSummary(
            @Param("warehouseId") Long warehouseId,
            @Param("materialId") Long materialId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query(value = """
        SELECT 
            ir.record_no as recordNo,
            ir.record_type as recordType,
            ir.biz_type as bizType,
            ir.biz_no as bizNo,
            ir.created_at as recordTime,
            m.id as materialId,
            m.material_code as materialCode,
            m.material_name as materialName,
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            wl.location_code as locationCode,
            ir.quantity as quantity,
            ir.before_quantity as beforeQuantity,
            ir.after_quantity as afterQuantity,
            ir.unit as unit,
            ir.batch_no as batchNo,
            u.real_name as operatorName,
            ir.remark as remark
        FROM wms_inventory_record ir
        JOIN wms_material m ON ir.material_id = m.id
        JOIN wms_warehouse w ON ir.warehouse_id = w.id
        LEFT JOIN wms_warehouse_location wl ON ir.location_id = wl.id
        LEFT JOIN wms_user u ON ir.operator_id = u.id
        WHERE ir.is_deleted = false
        AND (:warehouseId IS NULL OR ir.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR ir.material_id = :materialId)
        AND (:startDate IS NULL OR DATE(ir.created_at) >= :startDate)
        AND (:endDate IS NULL OR DATE(ir.created_at) <= :endDate)
        AND (:recordType IS NULL OR ir.record_type = :recordType)
        ORDER BY ir.created_at DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM wms_inventory_record ir
        WHERE ir.is_deleted = false
        AND (:warehouseId IS NULL OR ir.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR ir.material_id = :materialId)
        AND (:startDate IS NULL OR DATE(ir.created_at) >= :startDate)
        AND (:endDate IS NULL OR DATE(ir.created_at) <= :endDate)
        AND (:recordType IS NULL OR ir.record_type = :recordType)
        """,
        nativeQuery = true)
    Page<InventoryDetailProjection> getInventoryDetail(
            @Param("warehouseId") Long warehouseId,
            @Param("materialId") Long materialId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("recordType") String recordType,
            Pageable pageable);

    @Query(value = """
        SELECT 
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            m.id as materialId,
            m.material_code as materialCode,
            m.material_name as materialName,
            mc.category_name as categoryName,
            i.batch_no as batchNo,
            i.production_date as productionDate,
            i.expiry_date as expiryDate,
            i.quantity as quantity,
            i.unit as unit,
            DATEDIFF(CURDATE(), COALESCE(i.production_date, i.created_at)) as ageDays,
            i.quantity * m.purchase_price as amount
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        LEFT JOIN wms_material_category mc ON m.category_id = mc.id
        JOIN wms_warehouse w ON i.warehouse_id = w.id
        WHERE i.is_deleted = false
        AND i.quantity > 0
        AND (:warehouseId IS NULL OR i.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR i.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:maxAge IS NULL OR DATEDIFF(CURDATE(), COALESCE(i.production_date, i.created_at)) <= :maxAge)
        ORDER BY ageDays DESC, w.warehouse_name, m.material_code
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        WHERE i.is_deleted = false
        AND i.quantity > 0
        AND (:warehouseId IS NULL OR i.warehouse_id = :warehouseId)
        AND (:materialId IS NULL OR i.material_id = :materialId)
        AND (:categoryId IS NULL OR m.category_id = :categoryId)
        AND (:maxAge IS NULL OR DATEDIFF(CURDATE(), COALESCE(i.production_date, i.created_at)) <= :maxAge)
        """,
        nativeQuery = true)
    Page<InventoryAgeProjection> getInventoryAge(
            @Param("warehouseId") Long warehouseId,
            @Param("materialId") Long materialId,
            @Param("categoryId") Long categoryId,
            @Param("maxAge") Integer maxAge,
            Pageable pageable);

    @Query(value = """
        SELECT 
            w.id as warehouseId,
            w.warehouse_name as warehouseName,
            SUM(i.quantity) as totalQuantity,
            SUM(i.quantity * COALESCE(m.purchase_price, 0)) as totalAmount
        FROM wms_warehouse w
        LEFT JOIN wms_inventory i ON w.id = i.warehouse_id AND i.is_deleted = false
        LEFT JOIN wms_material m ON i.material_id = m.id
        WHERE w.is_deleted = false
        AND w.status = 1
        GROUP BY w.id, w.warehouse_name
        ORDER BY totalAmount DESC
        """, nativeQuery = true)
    List<WarehouseDistributionProjection> getWarehouseDistribution();

    @Query(value = """
        SELECT 
            DATE_FORMAT(sio.order_date, '%Y-%m-%d') as date,
            SUM(sii.quantity) as quantity,
            SUM(sii.total_amount) as amount
        FROM wms_stock_in_order sio
        JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id
        WHERE sio.is_deleted = false
        AND sio.status = 'COMPLETED'
        AND sio.order_date >= :startDate
        AND sio.order_date <= :endDate
        GROUP BY DATE_FORMAT(sio.order_date, '%Y-%m-%d')
        ORDER BY date
        """, nativeQuery = true)
    List<TrendDataProjection> getStockInTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT 
            DATE_FORMAT(soo.order_date, '%Y-%m-%d') as date,
            SUM(soi.quantity) as quantity,
            SUM(soi.total_amount) as amount
        FROM wms_stock_out_order soo
        JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id
        WHERE soo.is_deleted = false
        AND soo.status = 'COMPLETED'
        AND soo.order_date >= :startDate
        AND soo.order_date <= :endDate
        GROUP BY DATE_FORMAT(soo.order_date, '%Y-%m-%d')
        ORDER BY date
        """, nativeQuery = true)
    List<TrendDataProjection> getStockOutTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT 
            mc.category_name as categoryName,
            SUM(i.quantity * COALESCE(m.purchase_price, 0)) as amount
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        LEFT JOIN wms_material_category mc ON m.category_id = mc.id
        WHERE i.is_deleted = false
        AND i.quantity > 0
        GROUP BY mc.category_name
        ORDER BY amount DESC
        """, nativeQuery = true)
    List<CategoryDistributionProjection> getCategoryDistribution();

    @Query(value = """
        SELECT 
            COALESCE(SUM(i.quantity * m.purchase_price), 0) as totalAmount,
            COALESCE(SUM(i.quantity), 0) as totalQuantity,
            COUNT(DISTINCT i.material_id) as materialCount,
            (SELECT COUNT(*) FROM wms_warehouse WHERE is_deleted = false AND status = 1) as warehouseCount,
            (SELECT COALESCE(SUM(sii.quantity), 0) FROM wms_stock_in_order sio JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id WHERE sio.is_deleted = false AND sio.status = 'COMPLETED' AND sio.order_date = CURDATE()) as todayStockInQuantity,
            (SELECT COALESCE(SUM(soi.quantity), 0) FROM wms_stock_out_order soo JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id WHERE soo.is_deleted = false AND soo.status = 'COMPLETED' AND soo.order_date = CURDATE()) as todayStockOutQuantity,
            (SELECT COALESCE(SUM(sii.total_amount), 0) FROM wms_stock_in_order sio JOIN wms_stock_in_item sii ON sio.id = sii.stock_in_order_id WHERE sio.is_deleted = false AND sio.status = 'COMPLETED' AND sio.order_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')) as monthStockInAmount,
            (SELECT COALESCE(SUM(soi.total_amount), 0) FROM wms_stock_out_order soo JOIN wms_stock_out_item soi ON soo.id = soi.stock_out_order_id WHERE soo.is_deleted = false AND soo.status = 'COMPLETED' AND soo.order_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')) as monthStockOutAmount,
            (SELECT COUNT(DISTINCT i2.material_id) FROM wms_inventory i2 JOIN wms_material m2 ON i2.material_id = m2.id WHERE i2.is_deleted = false AND i2.quantity <= m2.safety_stock) as lowStockCount,
            (SELECT COUNT(*) FROM wms_inventory i3 WHERE i3.is_deleted = false AND i3.expiry_date IS NOT NULL AND i3.expiry_date <= CURDATE()) as expiredCount
        FROM wms_inventory i
        JOIN wms_material m ON i.material_id = m.id
        WHERE i.is_deleted = false
        """, nativeQuery = true)
    DashboardSummaryProjection getDashboardSummary();

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

    interface DashboardSummaryProjection {
        BigDecimal getTotalAmount();
        BigDecimal getTotalQuantity();
        Long getMaterialCount();
        Long getWarehouseCount();
        BigDecimal getTodayStockInQuantity();
        BigDecimal getTodayStockOutQuantity();
        BigDecimal getMonthStockInAmount();
        BigDecimal getMonthStockOutAmount();
        Long getLowStockCount();
        Long getExpiredCount();
    }
}
