package com.andy.warehouse.repository;

import com.andy.warehouse.entity.InventoryCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long>, JpaSpecificationExecutor<InventoryCheck> {

    Optional<InventoryCheck> findByCheckNo(String checkNo);

    @Query("SELECT ic FROM InventoryCheck ic WHERE ic.deleted = false AND ic.warehouse.id = :warehouseId")
    List<InventoryCheck> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT ic FROM InventoryCheck ic WHERE ic.deleted = false " +
           "AND (:warehouseId IS NULL OR ic.warehouse.id = :warehouseId) " +
           "AND (:startDate IS NULL OR ic.checkDate >= :startDate) " +
           "AND (:endDate IS NULL OR ic.checkDate <= :endDate) " +
           "AND (:status IS NULL OR ic.status = :status)")
    Page<InventoryCheck> searchChecks(@Param("warehouseId") Long warehouseId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("status") Integer status,
                                      Pageable pageable);

    @Query("SELECT ic FROM InventoryCheck ic JOIN FETCH ic.items WHERE ic.id = :id")
    Optional<InventoryCheck> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT ic FROM InventoryCheck ic WHERE ic.deleted = false AND ic.status = 1 ORDER BY ic.checkDate DESC")
    List<InventoryCheck> findCompletedChecks();
}
