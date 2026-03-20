package com.warehouse.management.service.impl;

import com.warehouse.management.entity.OutStock;
import com.warehouse.management.entity.OutStockDetail;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.repository.OutStockRepository;
import com.warehouse.management.repository.InventoryRepository;
import com.warehouse.management.service.OutStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutStockServiceImpl implements OutStockService {

    private final OutStockRepository outStockRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public OutStock save(OutStock outStock) {
        return outStockRepository.save(outStock);
    }

    @Override
    @Transactional
    public OutStock createOutStock(OutStock outStock) {
        if (outStock.getDetails() != null) {
            for (OutStockDetail detail : outStock.getDetails()) {
                detail.setOutStock(outStock);
            }
        }
        checkInventory(outStock);
        OutStock saved = outStockRepository.save(outStock);
        updateInventory(saved);
        return saved;
    }

    private void checkInventory(OutStock outStock) {
        if (outStock.getDetails() == null) {
            return;
        }

        for (OutStockDetail detail : outStock.getDetails()) {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByWarehouseIdAndMaterialId(
                    outStock.getWarehouse().getId(),
                    detail.getMaterial().getId()
            );

            if (inventoryOpt.isEmpty()) {
                throw new RuntimeException("物资不存在库存: " + detail.getMaterial().getName());
            }

            Inventory inventory = inventoryOpt.get();
            if (inventory.getQuantity().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("物资库存不足: " + detail.getMaterial().getName() +
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
            Optional<Inventory> existing = inventoryRepository.findByWarehouseIdAndMaterialId(
                    outStock.getWarehouse().getId(),
                    detail.getMaterial().getId()
            );

            if (existing.isPresent()) {
                Inventory inventory = existing.get();
                inventory.setQuantity(inventory.getQuantity().subtract(detail.getQuantity()));
                inventory.setTotalPrice(inventory.getQuantity().multiply(inventory.getUnitPrice()));
                inventoryRepository.save(inventory);
            }
        }
    }

    @Override
    public Optional<OutStock> findById(Long id) {
        return outStockRepository.findById(id);
    }

    @Override
    public List<OutStock> findAll() {
        return outStockRepository.findAll();
    }

    @Override
    public Page<OutStock> findAll(Pageable pageable) {
        return outStockRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        outStockRepository.deleteById(id);
    }

    @Override
    public boolean existsByOrderNo(String orderNo) {
        return outStockRepository.existsByOrderNo(orderNo);
    }
}
