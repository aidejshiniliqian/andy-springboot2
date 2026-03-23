package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(Long id);
    List<Warehouse> findAll();
    Page<Warehouse> findAll(Page<Warehouse> pageable);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
