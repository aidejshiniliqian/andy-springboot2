package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    boolean existsByWarehouseCode(String warehouseCode);

    @Query("SELECT w FROM Warehouse w WHERE w.isDeleted = false AND " +
           "(:warehouseCode IS NULL OR w.warehouseCode LIKE %:warehouseCode%) AND " +
           "(:warehouseName IS NULL OR w.warehouseName LIKE %:warehouseName%) AND " +
           "(:status IS NULL OR w.status = :status)")
    Page<Warehouse> findByConditions(@Param("warehouseCode") String warehouseCode,
                                     @Param("warehouseName") String warehouseName,
                                     @Param("status") Integer status,
                                     Pageable pageable);

    List<Warehouse> findByStatus(Integer status);
}
