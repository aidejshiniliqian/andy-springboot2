package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.OutStock;

import java.util.List;
import java.util.Optional;

public interface OutStockService {
    OutStock save(OutStock outStock);
    OutStock createOutStock(OutStock outStock);
    Optional<OutStock> findById(Long id);
    List<OutStock> findAll();
    Page<OutStock> findAll(Page<OutStock> pageable);
    void deleteById(Long id);
    boolean existsByOrderNo(String orderNo);
}
