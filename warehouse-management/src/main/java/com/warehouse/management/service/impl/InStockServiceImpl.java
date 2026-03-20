package com.warehouse.management.service.impl;

import com.warehouse.management.entity.InStock;
import com.warehouse.management.entity.InStockDetail;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.repository.InStockRepository;
import com.warehouse.management.repository.InventoryRepository;
import com.warehouse.management.service.InStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InStockServiceImpl implements InStockService {

    private final InStockRepository inStockRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public InStock save(InStock inStock) {
        return inStockRepository.save(inStock);
    }

    @Override
    @Transactional
    public InStock createInStock(InStock inStock) {
        if (inStock.getDetails() != null) {
            for (InStockDetail detail : inStock.getDetails()) {
                detail.setInStock(inStock);
            }
        }
        InStock saved = inStockRepository.save(inStock);
        updateInventory(saved);
        return saved;
    }

    private void updateInventory(InStock inStock) {
        if (inStock.getDetails() == null) {
            return;
        }

        for (InStockDetail detail : inStock.getDetails()) {
            Optional<Inventory> existing = inventoryRepository.findByWarehouseIdAndMaterialId(
                    inStock.getWarehouse().getId(),
                    detail.getMaterial().getId()
            );

            if (existing.isPresent()) {
                Inventory inventory = existing.get();
                inventory.setQuantity(inventory.getQuantity().add(detail.getQuantity()));
                inventory.setTotalPrice(inventory.getQuantity().multiply(inventory.getUnitPrice()));
                inventoryRepository.save(inventory);
            } else {
                Inventory inventory = new Inventory();
                inventory.setWarehouse(inStock.getWarehouse());
                inventory.setMaterial(detail.getMaterial());
                inventory.setQuantity(detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setTotalPrice(detail.getTotalPrice());
                inventoryRepository.save(inventory);
            }
        }
    }

    @Override
    public Optional<InStock> findById(Long id) {
        return inStockRepository.findById(id);
    }

    @Override
    public List<InStock> findAll() {
        return inStockRepository.findAll();
    }

    @Override
    public Page<InStock> findAll(Pageable pageable) {
        return inStockRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        inStockRepository.deleteById(id);
    }

    @Override
    public boolean existsByOrderNo(String orderNo) {
        return inStockRepository.existsByOrderNo(orderNo);
    }
}
