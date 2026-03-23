package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.mapper.InventoryMapper;
import com.warehouse.management.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Override
    public Inventory save(Inventory inventory) {
        saveOrUpdate(inventory);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Inventory> findByWarehouseIdAndMaterialId(Long warehouseId, Long materialId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getMaterialId, materialId);
        return Optional.ofNullable(getOne(wrapper));
    }

    @Override
    public List<Inventory> findAll() {
        return list();
    }

    @Override
    public Page<Inventory> findAll(Page<Inventory> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }
}
