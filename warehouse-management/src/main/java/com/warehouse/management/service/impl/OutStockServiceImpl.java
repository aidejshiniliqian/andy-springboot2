package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.OutStock;
import com.warehouse.management.entity.OutStockDetail;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.mapper.OutStockDetailMapper;
import com.warehouse.management.mapper.OutStockMapper;
import com.warehouse.management.mapper.InventoryMapper;
import com.warehouse.management.service.OutStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutStockServiceImpl extends ServiceImpl<OutStockMapper, OutStock> implements OutStockService {

    private final OutStockDetailMapper outStockDetailMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public OutStock save(OutStock outStock) {
        saveOrUpdate(outStock);
        return outStock;
    }

    @Override
    @Transactional
    public OutStock createOutStock(OutStock outStock) {
        checkInventory(outStock);
        saveOrUpdate(outStock);
        
        if (outStock.getDetails() != null) {
            for (OutStockDetail detail : outStock.getDetails()) {
                detail.setOutStockId(outStock.getId());
                outStockDetailMapper.insert(detail);
            }
        }
        updateInventory(outStock);
        return outStock;
    }

    private void checkInventory(OutStock outStock) {
        if (outStock.getDetails() == null) {
            return;
        }

        for (OutStockDetail detail : outStock.getDetails()) {
            LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Inventory::getWarehouseId, outStock.getWarehouseId())
                   .eq(Inventory::getMaterialId, detail.getMaterialId());
            
            Inventory inventory = inventoryMapper.selectOne(wrapper);

            if (inventory == null) {
                throw new RuntimeException("物资不存在库存: 物资ID=" + detail.getMaterialId());
            }

            if (inventory.getQuantity().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("物资库存不足: 物资ID=" + detail.getMaterialId() +
                        ", 现有库存: " + inventory.getQuantity() +
                        ", 需要: " + detail.getQuantity());
            }
        }
    }

    private void updateInventory(OutStock outStock) {
        if (outStock.getDetails() == null) {
            return;
        }

        for (OutStockDetail detail : outStock.getDetails()) {
            LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Inventory::getWarehouseId, outStock.getWarehouseId())
                   .eq(Inventory::getMaterialId, detail.getMaterialId());
            
            Inventory existing = inventoryMapper.selectOne(wrapper);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity().subtract(detail.getQuantity()));
                existing.setTotalPrice(existing.getQuantity().multiply(existing.getUnitPrice()));
                inventoryMapper.updateById(existing);
            }
        }
    }

    @Override
    public Optional<OutStock> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<OutStock> findAll() {
        return list();
    }

    @Override
    public Page<OutStock> findAll(Page<OutStock> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }

    @Override
    public boolean existsByOrderNo(String orderNo) {
        LambdaQueryWrapper<OutStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutStock::getOrderNo, orderNo);
        return count(wrapper) > 0;
    }
}
