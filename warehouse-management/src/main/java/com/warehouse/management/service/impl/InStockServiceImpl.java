package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.InStock;
import com.warehouse.management.entity.InStockDetail;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.mapper.InStockDetailMapper;
import com.warehouse.management.mapper.InStockMapper;
import com.warehouse.management.mapper.InventoryMapper;
import com.warehouse.management.service.InStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InStockServiceImpl extends ServiceImpl<InStockMapper, InStock> implements InStockService {

    private final InStockDetailMapper inStockDetailMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public InStock save(InStock inStock) {
        saveOrUpdate(inStock);
        return inStock;
    }

    @Override
    @Transactional
    public InStock createInStock(InStock inStock) {
        saveOrUpdate(inStock);
        
        if (inStock.getDetails() != null) {
            for (InStockDetail detail : inStock.getDetails()) {
                detail.setInStockId(inStock.getId());
                inStockDetailMapper.insert(detail);
            }
        }
        updateInventory(inStock);
        return inStock;
    }

    private void updateInventory(InStock inStock) {
        if (inStock.getDetails() == null) {
            return;
        }

        for (InStockDetail detail : inStock.getDetails()) {
            LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Inventory::getWarehouseId, inStock.getWarehouseId())
                   .eq(Inventory::getMaterialId, detail.getMaterialId());
            
            Inventory existing = inventoryMapper.selectOne(wrapper);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity().add(detail.getQuantity()));
                existing.setTotalPrice(existing.getQuantity().multiply(existing.getUnitPrice()));
                inventoryMapper.updateById(existing);
            } else {
                Inventory inventory = new Inventory();
                inventory.setWarehouseId(inStock.getWarehouseId());
                inventory.setMaterialId(detail.getMaterialId());
                inventory.setQuantity(detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setTotalPrice(detail.getTotalPrice());
                inventoryMapper.insert(inventory);
            }
        }
    }

    @Override
    public Optional<InStock> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<InStock> findAll() {
        return list();
    }

    @Override
    public Page<InStock> findAll(Page<InStock> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }

    @Override
    public boolean existsByOrderNo(String orderNo) {
        LambdaQueryWrapper<InStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InStock::getOrderNo, orderNo);
        return count(wrapper) > 0;
    }
}
