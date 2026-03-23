package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.Stock;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.mapper.MaterialMapper;
import com.andy.warehouse.mapper.StockMapper;
import com.andy.warehouse.mapper.WarehouseMapper;
import com.andy.warehouse.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockMapper stockMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;

    @Override
    public Stock getByWarehouseAndMaterial(Long warehouseId, Long materialId) {
        return stockMapper.findByWarehouseAndMaterial(warehouseId, materialId);
    }

    @Override
    public List<Stock> getByWarehouseId(Long warehouseId) {
        return stockMapper.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Stock> getByMaterialId(Long materialId) {
        return stockMapper.findByMaterialId(materialId);
    }

    @Override
    public Integer getTotalQuantityByMaterialId(Long materialId) {
        return stockMapper.getTotalQuantityByMaterialId(materialId);
    }

    @Override
    @Transactional
    public void addStock(Long warehouseId, Long materialId, Integer quantity, String batchNo, String position) {
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException("物资不存在");
        }
        Stock stock = stockMapper.findByWarehouseAndMaterial(warehouseId, materialId);
        if (stock == null) {
            stock = new Stock();
            stock.setWarehouseId(warehouseId);
            stock.setMaterialId(materialId);
            stock.setQuantity(quantity);
            stock.setAvailableQuantity(quantity);
            stock.setLockedQuantity(0);
            stock.setBatchNo(batchNo);
            stock.setPosition(position);
            stockMapper.insert(stock);
        } else {
            stock.setQuantity(stock.getQuantity() + quantity);
            stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
            stockMapper.updateById(stock);
        }
    }

    @Override
    @Transactional
    public void subtractStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockMapper.findByWarehouseAndMaterial(warehouseId, materialId);
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }
        if (stock.getQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stockMapper.updateById(stock);
    }

    @Override
    @Transactional
    public void lockStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockMapper.findByWarehouseAndMaterial(warehouseId, materialId);
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }
        if (stock.getAvailableQuantity() < quantity) {
            throw new BusinessException("可用库存不足");
        }
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stock.setLockedQuantity(stock.getLockedQuantity() + quantity);
        stockMapper.updateById(stock);
    }

    @Override
    @Transactional
    public void unlockStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockMapper.findByWarehouseAndMaterial(warehouseId, materialId);
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }
        if (stock.getLockedQuantity() < quantity) {
            throw new BusinessException("锁定库存不足");
        }
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        stock.setLockedQuantity(stock.getLockedQuantity() - quantity);
        stockMapper.updateById(stock);
    }
}
