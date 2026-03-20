package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT s FROM Stock s JOIN FETCH s.material m JOIN FETCH m.category WHERE s.quantity > 0")
    List<Stock> findAllWithMaterialAndCategory();

    @Query("SELECT s FROM Stock s JOIN FETCH s.material m JOIN FETCH m.category WHERE s.warehouse.id = :warehouseId AND s.quantity > 0")
    List<Stock> findByWarehouseIdWithMaterialAndCategory(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COUNT(DISTINCT s.material.id) FROM Stock s WHERE s.quantity > 0")
    Integer countDistinctMaterials();

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s")
    Integer getTotalQuantity();

    @Query("SELECT COALESCE(SUM(s.quantity * m.price), 0) FROM Stock s JOIN s.material m")
    BigDecimal getTotalAmount();

    @Query("SELECT s.warehouse.id as warehouseId, COUNT(DISTINCT s.material.id) as materialCount, " +
           "SUM(s.quantity) as totalQuantity FROM Stock s WHERE s.quantity > 0 GROUP BY s.warehouse.id")
    List<Object[]> getStockDistributionByWarehouse();

    @Query("SELECT m FROM Material m WHERE m.deleted = false AND m.safetyStock IS NOT NULL AND m.safetyStock > 0")
    List<com.andy.warehouse.entity.Material> findMaterialsForStockAlert();
}
