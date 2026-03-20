package com.warehouse.management.repository;

import com.warehouse.management.entity.InStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InStockRepository extends JpaRepository<InStock, Long>, JpaSpecificationExecutor<InStock> {
    boolean existsByOrderNo(String orderNo);

    /**
     * 根据入库时间范围查询入库单
     * @param start 开始时间
     * @param end 结束时间
     * @return 入库单列表
     */
    List<InStock> findByInStockTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据仓库ID和物料ID查询最早的入库时间
     * @param warehouseId 仓库ID
     * @param materialId 物料ID
     * @return 入库单列表
     */
    @Query("SELECT i FROM InStock i JOIN i.details d WHERE i.warehouse.id = ?1 AND d.material.id = ?2 ORDER BY i.inStockTime ASC")
    List<InStock> findByWarehouseIdAndMaterialIdOrderByInStockTimeAsc(Long warehouseId, Long materialId);
}
