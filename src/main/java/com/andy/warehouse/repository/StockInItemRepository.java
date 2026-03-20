package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockInItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInItemRepository extends JpaRepository<StockInItem, Long> {

    List<StockInItem> findByStockInOrderId(Long stockInOrderId);

    List<StockInItem> findByMaterialIdAndStatus(Long materialId, String status);
}
