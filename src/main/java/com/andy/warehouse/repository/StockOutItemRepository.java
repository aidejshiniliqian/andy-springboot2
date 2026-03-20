package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockOutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutItemRepository extends JpaRepository<StockOutItem, Long> {

    List<StockOutItem> findByStockOutOrderId(Long stockOutOrderId);

    List<StockOutItem> findByMaterialIdAndStatus(Long materialId, String status);
}
