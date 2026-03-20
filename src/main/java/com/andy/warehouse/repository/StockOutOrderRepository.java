package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockOutOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
