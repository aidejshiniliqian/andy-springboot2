package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.InStock;

import java.util.List;
import java.util.Optional;

public interface InStockService {
    InStock save(InStock inStock);
    InStock createInStock(InStock inStock);
    Optional<InStock> findById(Long id);
    List<InStock> findAll();
    Page<InStock> findAll(Page<InStock> pageable);
    void deleteById(Long id);
    boolean existsByOrderNo(String orderNo);
}
