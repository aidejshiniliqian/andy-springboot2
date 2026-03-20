package com.andy.warehouse.repository;

import com.andy.warehouse.entity.WarehouseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, Long> {

    Optional<WarehouseLocation> findByLocationCodeAndWarehouseId(String locationCode, Long warehouseId);

    List<WarehouseLocation> findByWarehouseIdAndStatus(Long warehouseId, Integer status);

    List<WarehouseLocation> findByZoneIdAndStatus(Long zoneId, Integer status);

    boolean existsByLocationCodeAndWarehouseId(String locationCode, Long warehouseId);
}
