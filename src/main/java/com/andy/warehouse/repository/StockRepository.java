package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {

    @Query("SELECT s FROM Stock s WHERE s.warehouse.id = :warehouseId AND s.material.id = :materialId")
    Optional<Stock> findByWarehouseAndMaterial(@Param("warehouseId") Long warehouseId, @Param("materialId") Long materialId);

    @Query("SELECT s FROM Stock s WHERE s.warehouse.id = :warehouseId")
    List<Stock> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT s FROM Stock s WHERE s.material.id = :materialId")
    List<Stock> findByMaterialId(@Param("materialId") Long materialId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.material.id = :materialId")
    Integer getTotalQuantityByMaterialId(@Param("materialId") Long materialId);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity + :quantity, s.availableQuantity = s.availableQuantity + :quantity WHERE s.id = :id")
    void addQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :quantity, s.availableQuantity = s.availableQuantity - :quantity WHERE s.id = :id AND s.quantity >= :quantity")
    int subtractQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Stock s SET s.lockedQuantity = s.lockedQuantity + :quantity, s.availableQuantity = s.availableQuantity - :quantity WHERE s.id = :id AND s.availableQuantity >= :quantity")
    int lockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Stock s SET s.lockedQuantity = s.lockedQuantity - :quantity, s.availableQuantity = s.availableQuantity + :quantity WHERE s.id = :id AND s.lockedQuantity >= :quantity")
    int unlockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
}
