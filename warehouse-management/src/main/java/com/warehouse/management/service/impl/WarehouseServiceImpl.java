package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Warehouse;
import com.warehouse.management.mapper.WarehouseMapper;
import com.warehouse.management.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public Warehouse save(Warehouse warehouse) {
        this.saveOrUpdate(warehouse);
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return Optional.ofNullable(this.getById(id));
    }

    @Override
    public List<Warehouse> findAll() {
        return this.list();
    }

    @Override
    public Page<Warehouse> findAll(Page<Warehouse> pageable) {
        return this.page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getCode, code);
        return this.count(wrapper) > 0;
    }
}
