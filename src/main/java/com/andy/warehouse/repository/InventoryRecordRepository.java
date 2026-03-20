package com.andy.warehouse.repository;

import com.andy.warehouse.entity.InventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {

    @Query("SELECT ir FROM InventoryRecord ir WHERE ir.isDeleted = false AND " +
           "(:recordNo IS NULL OR ir.recordNo LIKE %:recordNo%) AND " +
           "(:recordType IS NULL OR ir.recordType = :recordType) AND " +
           "(:materialId IS NULL OR ir.material.id = :materialId) AND " +
           "(:warehouseId IS NULL OR ir.warehouse.id = :warehouseId) AND " +
           "(:startTime IS NULL OR ir.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR ir.createdAt <= :endTime)")
    Page<InventoryRecord> findByConditions(@Param("recordNo") String recordNo,
                                           @Param("recordType") String recordType,
                                           @Param("materialId") Long materialId,
                                           @Param("warehouseId") Long warehouseId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           Pageable pageable);

    @Query("SELECT ir FROM InventoryRecord ir WHERE ir.material.id = :materialId " +
           "AND ir.warehouse.id = :warehouseId ORDER BY ir.createdAt DESC")
    Page<InventoryRecord> findByMaterialAndWarehouse(@Param("materialId") Long materialId,
                                                     @Param("warehouseId") Long warehouseId,
                                                     Pageable pageable);
}
