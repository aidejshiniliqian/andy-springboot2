package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.management.dto.*;
import com.warehouse.management.entity.*;
import com.warehouse.management.mapper.*;
import com.warehouse.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 * 实现各类报表查询逻辑
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryMapper inventoryMapper;
    private final InStockMapper inStockMapper;
    private final OutStockMapper outStockMapper;
    private final InStockDetailMapper inStockDetailMapper;
    private final OutStockDetailMapper outStockDetailMapper;
    private final MaterialMapper materialMapper;
    private final WarehouseMapper warehouseMapper;

    @Override
    public List<InventorySummaryDTO> getInventorySummary(Long warehouseId, Long categoryId, Long materialId) {
        List<Inventory> inventories = inventoryMapper.selectList(null);

        return inventories.stream()
                .filter(inv -> warehouseId == null || inv.getWarehouseId().equals(warehouseId))
                .filter(inv -> materialId == null || inv.getMaterialId().equals(materialId))
                .map(this::convertToInventorySummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockTransactionDTO> getStockTransactions(LocalDateTime startDate, LocalDateTime endDate,
                                                           Long warehouseId, Long materialId, String transactionType) {
        List<StockTransactionDTO> result = new ArrayList<>();

        // 查询入库记录
        if (transactionType == null || "IN".equals(transactionType)) {
            LambdaQueryWrapper<InStock> inStockWrapper = new LambdaQueryWrapper<>();
            inStockWrapper.between(InStock::getInStockTime, startDate, endDate);
            if (warehouseId != null) {
                inStockWrapper.eq(InStock::getWarehouseId, warehouseId);
            }
            List<InStock> inStocks = inStockMapper.selectList(inStockWrapper);
            
            inStocks.forEach(inStock -> {
                LambdaQueryWrapper<InStockDetail> detailWrapper = new LambdaQueryWrapper<>();
                detailWrapper.eq(InStockDetail::getInStockId, inStock.getId());
                if (materialId != null) {
                    detailWrapper.eq(InStockDetail::getMaterialId, materialId);
                }
                List<InStockDetail> details = inStockDetailMapper.selectList(detailWrapper);
                details.forEach(detail -> result.add(convertToTransactionDTO(inStock, detail, "IN")));
            });
        }

        // 查询出库记录
        if (transactionType == null || "OUT".equals(transactionType)) {
            LambdaQueryWrapper<OutStock> outStockWrapper = new LambdaQueryWrapper<>();
            outStockWrapper.between(OutStock::getOutStockTime, startDate, endDate);
            if (warehouseId != null) {
                outStockWrapper.eq(OutStock::getWarehouseId, warehouseId);
            }
            List<OutStock> outStocks = outStockMapper.selectList(outStockWrapper);
            
            outStocks.forEach(outStock -> {
                LambdaQueryWrapper<OutStockDetail> detailWrapper = new LambdaQueryWrapper<>();
                detailWrapper.eq(OutStockDetail::getOutStockId, outStock.getId());
                if (materialId != null) {
                    detailWrapper.eq(OutStockDetail::getMaterialId, materialId);
                }
                List<OutStockDetail> details = outStockDetailMapper.selectList(detailWrapper);
                details.forEach(detail -> result.add(convertToTransactionDTO(outStock, detail, "OUT")));
            });
        }

        // 按时间排序
        result.sort(Comparator.comparing(StockTransactionDTO::getTransactionTime).reversed());
        return result;
    }

    @Override
    public List<InventoryAgeDTO> getInventoryAge(Long warehouseId, Long categoryId, Integer minDays) {
        List<Inventory> inventories = inventoryMapper.selectList(null);
        LocalDateTime now = LocalDateTime.now();

        return inventories.stream()
                .filter(inv -> warehouseId == null || inv.getWarehouseId().equals(warehouseId))
                .map(inv -> {
                    // 查询该物料在该仓库的首次入库时间
                    LocalDateTime firstInTime = getFirstInTime(inv.getWarehouseId(), inv.getMaterialId());
                    long days = firstInTime != null ? Duration.between(firstInTime, now).toDays() : 0;

                    if (minDays != null && days < minDays) {
                        return null;
                    }

                    Material material = materialMapper.selectById(inv.getMaterialId());
                    Warehouse warehouse = warehouseMapper.selectById(inv.getWarehouseId());

                    return InventoryAgeDTO.builder()
                            .warehouseId(inv.getWarehouseId())
                            .warehouseName(warehouse != null ? warehouse.getName() : "未知")
                            .materialId(inv.getMaterialId())
                            .materialName(material != null ? material.getName() : "未知")
                            .materialCode(material != null ? material.getCode() : "未知")
                            .unit(material != null ? material.getUnit() : "")
                            .quantity(inv.getQuantity())
                            .unitPrice(inv.getUnitPrice())
                            .totalPrice(inv.getTotalPrice())
                            .firstInTime(firstInTime)
                            .daysInStock(days)
                            .ageLevel(getAgeLevel(days))
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<EfficiencyDTO> getInStockEfficiency(LocalDate startDate, LocalDate endDate, Long operatorId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<InStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(InStock::getInStockTime, start, end);
        List<InStock> inStocks = inStockMapper.selectList(wrapper);

        Map<LocalDate, List<InStock>> groupByDate = inStocks.stream()
                .collect(Collectors.groupingBy(is -> is.getInStockTime().toLocalDate()));

        return groupByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<InStock> stocks = entry.getValue();

                    long orderCount = stocks.size();
                    BigDecimal totalQty = stocks.stream()
                            .map(s -> {
                                LambdaQueryWrapper<InStockDetail> detailWrapper = new LambdaQueryWrapper<>();
                                detailWrapper.eq(InStockDetail::getInStockId, s.getId());
                                List<InStockDetail> details = inStockDetailMapper.selectList(detailWrapper);
                                return details.stream()
                                        .map(InStockDetail::getQuantity)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 简单计算效率：平均每单数量
                    BigDecimal efficiency = orderCount > 0 ?
                            totalQty.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP) :
                            BigDecimal.ZERO;

                    return EfficiencyDTO.builder()
                            .date(date)
                            .operator("未知")
                            .orderCount(orderCount)
                            .totalQuantity(totalQty)
                            .efficiencyRate(efficiency)
                            .operationType("上架")
                            .build();
                })
                .sorted(Comparator.comparing(EfficiencyDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<EfficiencyDTO> getOutStockEfficiency(LocalDate startDate, LocalDate endDate, Long operatorId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<OutStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(OutStock::getOutStockTime, start, end);
        List<OutStock> outStocks = outStockMapper.selectList(wrapper);

        Map<LocalDate, List<OutStock>> groupByDate = outStocks.stream()
                .collect(Collectors.groupingBy(os -> os.getOutStockTime().toLocalDate()));

        return groupByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<OutStock> stocks = entry.getValue();

                    long orderCount = stocks.size();
                    BigDecimal totalQty = stocks.stream()
                            .map(s -> {
                                LambdaQueryWrapper<OutStockDetail> detailWrapper = new LambdaQueryWrapper<>();
                                detailWrapper.eq(OutStockDetail::getOutStockId, s.getId());
                                List<OutStockDetail> details = outStockDetailMapper.selectList(detailWrapper);
                                return details.stream()
                                        .map(OutStockDetail::getQuantity)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 简单计算效率：平均每单数量
                    BigDecimal efficiency = orderCount > 0 ?
                            totalQty.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP) :
                            BigDecimal.ZERO;

                    return EfficiencyDTO.builder()
                            .date(date)
                            .operator("未知")
                            .orderCount(orderCount)
                            .totalQuantity(totalQty)
                            .efficiencyRate(efficiency)
                            .operationType("下架")
                            .build();
                })
                .sorted(Comparator.comparing(EfficiencyDTO::getDate))
                .collect(Collectors.toList());
    }

    private InventorySummaryDTO convertToInventorySummary(Inventory inventory) {
        Material material = materialMapper.selectById(inventory.getMaterialId());
        Warehouse warehouse = warehouseMapper.selectById(inventory.getWarehouseId());

        return InventorySummaryDTO.builder()
                .warehouseId(inventory.getWarehouseId())
                .warehouseName(warehouse != null ? warehouse.getName() : "未知")
                .materialId(inventory.getMaterialId())
                .materialName(material != null ? material.getName() : "未知")
                .materialCode(material != null ? material.getCode() : "未知")
                .spec(material != null ? material.getSpec() : "")
                .unit(material != null ? material.getUnit() : "")
                .quantity(inventory.getQuantity())
                .unitPrice(inventory.getUnitPrice())
                .totalPrice(inventory.getTotalPrice())
                .build();
    }

    private StockTransactionDTO convertToTransactionDTO(InStock inStock, InStockDetail detail, String type) {
        Material material = materialMapper.selectById(detail.getMaterialId());
        return StockTransactionDTO.builder()
                .transactionType(type)
                .transactionTime(inStock.getInStockTime())
                .orderNo(inStock.getOrderNo())
                .materialId(detail.getMaterialId())
                .materialName(material != null ? material.getName() : "未知")
                .materialCode(material != null ? material.getCode() : "未知")
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .warehouseId(inStock.getWarehouseId())
                .build();
    }

    private StockTransactionDTO convertToTransactionDTO(OutStock outStock, OutStockDetail detail, String type) {
        Material material = materialMapper.selectById(detail.getMaterialId());
        return StockTransactionDTO.builder()
                .transactionType(type)
                .transactionTime(outStock.getOutStockTime())
                .orderNo(outStock.getOrderNo())
                .materialId(detail.getMaterialId())
                .materialName(material != null ? material.getName() : "未知")
                .materialCode(material != null ? material.getCode() : "未知")
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .warehouseId(outStock.getWarehouseId())
                .build();
    }

    private LocalDateTime getFirstInTime(Long warehouseId, Long materialId) {
        LambdaQueryWrapper<InStock> inStockWrapper = new LambdaQueryWrapper<>();
        inStockWrapper.eq(InStock::getWarehouseId, warehouseId)
                .orderByAsc(InStock::getInStockTime)
                .last("LIMIT 1");
        List<InStock> inStocks = inStockMapper.selectList(inStockWrapper);

        for (InStock inStock : inStocks) {
            LambdaQueryWrapper<InStockDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(InStockDetail::getInStockId, inStock.getId())
                    .eq(InStockDetail::getMaterialId, materialId);
            InStockDetail detail = inStockDetailMapper.selectOne(detailWrapper);
            if (detail != null) {
                return inStock.getInStockTime();
            }
        }
        return null;
    }

    private String getAgeLevel(long days) {
        if (days <= 30) {
            return "正常";
        } else if (days <= 90) {
            return "滞销";
        } else if (days <= 180) {
            return "积压";
        } else {
            return "严重积压";
        }
    }
}
