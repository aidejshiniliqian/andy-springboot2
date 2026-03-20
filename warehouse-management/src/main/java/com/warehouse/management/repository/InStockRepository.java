package com.warehouse.management.repository;

import com.warehouse.management.entity.InStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InStockRepository extends JpaRepository<InStock, Long>, JpaSpecificationExecutor<InStock> {
    boolean existsByOrderNo(String orderNo);
}
