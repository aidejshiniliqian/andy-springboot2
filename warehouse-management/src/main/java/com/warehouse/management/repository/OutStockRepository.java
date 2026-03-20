package com.warehouse.management.repository;

import com.warehouse.management.entity.OutStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OutStockRepository extends JpaRepository<OutStock, Long>, JpaSpecificationExecutor<OutStock> {
    boolean existsByOrderNo(String orderNo);
}
