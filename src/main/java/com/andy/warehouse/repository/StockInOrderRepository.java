package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockInOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StockInOrderRepository extends JpaRepository<StockInOrder, Long> {

    Optional<StockInOrder> findByOrderNo(String orderNo);

    boolean existsByOrderNo(String orderNo);

    @Query("SELECT s FROM StockInOrder s WHERE s.isDeleted = false AND " +
           "(:orderNo IS NULL OR s.orderNo LIKE %:orderNo%) AND " +
           "(:orderType IS NULL OR s.orderType = :orderType) AND " +
           "(:warehouseId IS NULL OR s.warehouse.id = :warehouseId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:startDate IS NULL OR s.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.orderDate <= :endDate)")
    Page<StockInOrder> findByConditions(@Param("orderNo") String orderNo,
                                        @Param("orderType") String orderType,
                                        @Param("warehouseId") Long warehouseId,
                                        @Param("status") String status,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);
}
