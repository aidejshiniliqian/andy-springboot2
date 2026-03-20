package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockOutOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockOutOrderRepository extends JpaRepository<StockOutOrder, Long>, JpaSpecificationExecutor<StockOutOrder> {

    Optional<StockOutOrder> findByOrderNo(String orderNo);

    @Query("SELECT o FROM StockOutOrder o WHERE o.deleted = false AND o.warehouse.id = :warehouseId")
    List<StockOutOrder> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT o FROM StockOutOrder o WHERE o.deleted = false AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId)")
    Page<StockOutOrder> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);

    @Query("SELECT o FROM StockOutOrder o WHERE o.deleted = false AND o.status = :status")
    List<StockOutOrder> findByStatus(@Param("status") Integer status);

    @Query("SELECT o FROM StockOutOrder o JOIN FETCH o.items WHERE o.id = :id")
    Optional<StockOutOrder> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM StockOutOrder o WHERE o.deleted = false " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND (:startDate IS NULL OR o.orderDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.orderDate <= :endDate) " +
           "AND (:status IS NULL OR o.status = :status)")
    Page<StockOutOrder> searchOrders(@Param("warehouseId") Long warehouseId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("status") Integer status,
                                     Pageable pageable);

    @Query("SELECT COUNT(o) FROM StockOutOrder o WHERE o.deleted = false AND DATE(o.orderDate) = DATE(:date)")
    Integer countByOrderDate(@Param("date") LocalDateTime date);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM StockOutOrder o JOIN o.items i WHERE o.deleted = false AND DATE(o.orderDate) = DATE(:date)")
    Integer sumQuantityByOrderDate(@Param("date") LocalDateTime date);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM StockOutOrder o WHERE o.deleted = false AND DATE(o.orderDate) = DATE(:date)")
    BigDecimal sumAmountByOrderDate(@Param("date") LocalDateTime date);

    @Query("SELECT DATE(o.orderDate) as date, COALESCE(SUM(i.quantity), 0) as quantity, COALESCE(SUM(o.totalAmount), 0) as amount " +
           "FROM StockOutOrder o JOIN o.items i WHERE o.deleted = false " +
           "AND o.orderDate >= :startDate AND o.orderDate <= :endDate " +
           "GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailyStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
