package com.warehouse.management.repository;

import com.warehouse.management.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    Optional<Inventory> findByWarehouseIdAndMaterialId(Long warehouseId, Long materialId);
}
