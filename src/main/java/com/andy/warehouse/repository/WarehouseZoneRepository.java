package com.andy.warehouse.repository;

import com.andy.warehouse.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {

    Optional<WarehouseZone> findByZoneCodeAndWarehouseId(String zoneCode, Long warehouseId);

    List<WarehouseZone> findByWarehouseIdAndStatus(Long warehouseId, Integer status);

    boolean existsByZoneCodeAndWarehouseId(String zoneCode, Long warehouseId);
}
