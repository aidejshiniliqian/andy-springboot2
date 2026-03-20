package com.warehouse.management.service;

import com.warehouse.management.entity.OutStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OutStockService {
    OutStock save(OutStock outStock);
    OutStock createOutStock(OutStock outStock);
    Optional<OutStock> findById(Long id);
    List<OutStock> findAll();
    Page<OutStock> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByOrderNo(String orderNo);
}
