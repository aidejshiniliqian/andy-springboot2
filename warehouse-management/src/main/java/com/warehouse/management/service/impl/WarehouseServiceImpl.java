package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Warehouse;
import com.warehouse.management.repository.WarehouseRepository;
import com.warehouse.management.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public Warehouse save(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Page<Warehouse> findAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return warehouseRepository.existsByCode(code);
    }
}
