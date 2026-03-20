package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMaterialIdAndWarehouseIdAndLocationIdAndBatchNo(
            Long materialId, Long warehouseId, Long locationId, String batchNo);

    List<Inventory> findByMaterialIdAndWarehouseId(Long materialId, Long warehouseId);

    List<Inventory> findByWarehouseIdAndStatus(Long warehouseId, Integer status);

    @Query("SELECT i FROM Inventory i WHERE i.isDeleted = false AND " +
           "(:materialId IS NULL OR i.material.id = :materialId) AND " +
           "(:warehouseId IS NULL OR i.warehouse.id = :warehouseId) AND " +
           "(:locationId IS NULL OR i.location.id = :locationId) AND " +
           "(:batchNo IS NULL OR i.batchNo LIKE %:batchNo%)")
    Page<Inventory> findByConditions(@Param("materialId") Long materialId,
                                     @Param("warehouseId") Long warehouseId,
                                     @Param("locationId") Long locationId,
                                     @Param("batchNo") String batchNo,
                                     Pageable pageable);

    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.material.id = :materialId AND i.status = 1")
    java.math.BigDecimal getTotalQuantityByMaterialId(@Param("materialId") Long materialId);
}
