package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.entity.Material;
import com.andy.warehouse.entity.Stock;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.repository.MaterialRepository;
import com.andy.warehouse.repository.StockRepository;
import com.andy.warehouse.repository.WarehouseRepository;
import com.andy.warehouse.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;

    @Override
    public Stock getByWarehouseAndMaterial(Long warehouseId, Long materialId) {
        return stockRepository.findByWarehouseAndMaterial(warehouseId, materialId)
                .orElse(null);
    }

    @Override
    public List<Stock> getByWarehouseId(Long warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Stock> getByMaterialId(Long materialId) {
        return stockRepository.findByMaterialId(materialId);
    }

    @Override
    public Integer getTotalQuantityByMaterialId(Long materialId) {
        return stockRepository.getTotalQuantityByMaterialId(materialId);
    }

    @Override
    @Transactional
    public void addStock(Long warehouseId, Long materialId, Integer quantity, String batchNo, String position) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new BusinessException("物资不存在"));
        Stock stock = stockRepository.findByWarehouseAndMaterial(warehouseId, materialId)
                .orElse(null);
        if (stock == null) {
            stock = new Stock();
            stock.setWarehouse(warehouse);
            stock.setMaterial(material);
            stock.setQuantity(quantity);
            stock.setAvailableQuantity(quantity);
            stock.setLockedQuantity(0);
            stock.setBatchNo(batchNo);
            stock.setPosition(position);
        } else {
            stock.setQuantity(stock.getQuantity() + quantity);
            stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        }
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void subtractStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockRepository.findByWarehouseAndMaterial(warehouseId, materialId)
                .orElseThrow(() -> new BusinessException("库存不存在"));
        if (stock.getQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void lockStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockRepository.findByWarehouseAndMaterial(warehouseId, materialId)
                .orElseThrow(() -> new BusinessException("库存不存在"));
        if (stock.getAvailableQuantity() < quantity) {
            throw new BusinessException("可用库存不足");
        }
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stock.setLockedQuantity(stock.getLockedQuantity() + quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void unlockStock(Long warehouseId, Long materialId, Integer quantity) {
        Stock stock = stockRepository.findByWarehouseAndMaterial(warehouseId, materialId)
                .orElseThrow(() -> new BusinessException("库存不存在"));
        if (stock.getLockedQuantity() < quantity) {
            throw new BusinessException("锁定库存不足");
        }
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        stock.setLockedQuantity(stock.getLockedQuantity() - quantity);
        stockRepository.save(stock);
    }
}
