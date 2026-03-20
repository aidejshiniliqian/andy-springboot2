package com.andy.warehouse.repository;

import com.andy.warehouse.entity.InventoryCheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryCheckItemRepository extends JpaRepository<InventoryCheckItem, Long>, JpaSpecificationExecutor<InventoryCheckItem> {

    @Query("SELECT i FROM InventoryCheckItem i WHERE i.inventoryCheck.id = :checkId")
    List<InventoryCheckItem> findByCheckId(@Param("checkId") Long checkId);

    @Query("SELECT i FROM InventoryCheckItem i WHERE i.inventoryCheck.id = :checkId AND i.varianceQuantity != 0")
    List<InventoryCheckItem> findVarianceItemsByCheckId(@Param("checkId") Long checkId);
}
