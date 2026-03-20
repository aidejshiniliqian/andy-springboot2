package com.warehouse.management.repository;

import com.warehouse.management.entity.OutStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutStockRepository extends JpaRepository<OutStock, Long>, JpaSpecificationExecutor<OutStock> {
    boolean existsByOrderNo(String orderNo);

    /**
     * 根据出库时间范围查询出库单
     * @param start 开始时间
     * @param end 结束时间
     * @return 出库单列表
     */
    List<OutStock> findByOutStockTimeBetween(LocalDateTime start, LocalDateTime end);
}
