package com.warehouse.management.service;

import com.warehouse.management.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(Long id);
    List<Warehouse> findAll();
    Page<Warehouse> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
