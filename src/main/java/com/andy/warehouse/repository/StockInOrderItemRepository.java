package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockInOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInOrderItemRepository extends JpaRepository<StockInOrderItem, Long>, JpaSpecificationExecutor<StockInOrderItem> {

    @Query("SELECT i FROM StockInOrderItem i WHERE i.order.id = :orderId")
    List<StockInOrderItem> findByOrderId(@Param("orderId") Long orderId);
}
