package com.andy.warehouse.repository;

import com.andy.warehouse.entity.StockOutOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutOrderItemRepository extends JpaRepository<StockOutOrderItem, Long>, JpaSpecificationExecutor<StockOutOrderItem> {

    @Query("SELECT i FROM StockOutOrderItem i WHERE i.order.id = :orderId")
    List<StockOutOrderItem> findByOrderId(@Param("orderId") Long orderId);
}
