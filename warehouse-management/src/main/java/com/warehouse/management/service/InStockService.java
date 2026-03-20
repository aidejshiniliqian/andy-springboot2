package com.warehouse.management.service;

import com.warehouse.management.entity.InStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InStockService {
    InStock save(InStock inStock);
    InStock createInStock(InStock inStock);
    Optional<InStock> findById(Long id);
    List<InStock> findAll();
    Page<InStock> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByOrderNo(String orderNo);
}
