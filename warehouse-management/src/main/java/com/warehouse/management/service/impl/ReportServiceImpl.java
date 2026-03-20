package com.warehouse.management.service.impl;

import com.warehouse.management.dto.*;
import com.warehouse.management.entity.InStock;
import com.warehouse.management.entity.InStockDetail;
import com.warehouse.management.entity.Inventory;
import com.warehouse.management.entity.OutStock;
import com.warehouse.management.entity.OutStockDetail;
import com.warehouse.management.repository.InStockRepository;
import com.warehouse.management.repository.InventoryRepository;
import com.warehouse.management.repository.OutStockRepository;
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

    private final InventoryRepository inventoryRepository;
    private final InStockRepository inStockRepository;
    private final OutStockRepository outStockRepository;

    @Override
    public List<InventorySummaryDTO> getInventorySummary(Long warehouseId, Long categoryId, Long materialId) {
        List<Inventory> inventories = inventoryRepository.findAll();

        return inventories.stream()
                .filter(inv -> warehouseId == null || inv.getWarehouse().getId().equals(warehouseId))
                .filter(inv -> materialId == null || inv.getMaterial().getId().equals(materialId))
                .filter(inv -> categoryId == null ||
                        (inv.getMaterial().getCategory() != null &&
                                inv.getMaterial().getCategory().getId().equals(categoryId)))
                .map(this::convertToInventorySummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockTransactionDTO> getStockTransactions(LocalDateTime startDate, LocalDateTime endDate,
                                                           Long warehouseId, Long materialId, String transactionType) {
        List<StockTransactionDTO> result = new ArrayList<>();

        // 查询入库记录
        if (transactionType == null || "IN".equals(transactionType)) {
            List<InStock> inStocks = inStockRepository.findByInStockTimeBetween(startDate, endDate);
            inStocks.stream()
                    .filter(inStock -> warehouseId == null || inStock.getWarehouse().getId().equals(warehouseId))
                    .forEach(inStock -> {
                        if (inStock.getDetails() != null) {
                            inStock.getDetails().stream()
                                    .filter(detail -> materialId == null || detail.getMaterial().getId().equals(materialId))
                                    .forEach(detail -> result.add(convertToTransactionDTO(inStock, detail, "IN")));
                        }
                    });
        }

        // 查询出库记录
        if (transactionType == null || "OUT".equals(transactionType)) {
            List<OutStock> outStocks = outStockRepository.findByOutStockTimeBetween(startDate, endDate);
            outStocks.stream()
                    .filter(outStock -> warehouseId == null || outStock.getWarehouse().getId().equals(warehouseId))
                    .forEach(outStock -> {
                        if (outStock.getDetails() != null) {
                            outStock.getDetails().stream()
                                    .filter(detail -> materialId == null || detail.getMaterial().getId().equals(materialId))
                                    .forEach(detail -> result.add(convertToTransactionDTO(outStock, detail, "OUT")));
                        }
                    });
        }

        // 按时间排序
        result.sort(Comparator.comparing(StockTransactionDTO::getTransactionTime).reversed());
        return result;
    }

    @Override
    public List<InventoryAgeDTO> getInventoryAge(Long warehouseId, Long categoryId, Integer minDays) {
        List<Inventory> inventories = inventoryRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        return inventories.stream()
                .filter(inv -> warehouseId == null || inv.getWarehouse().getId().equals(warehouseId))
                .filter(inv -> categoryId == null ||
                        (inv.getMaterial().getCategory() != null &&
                                inv.getMaterial().getCategory().getId().equals(categoryId)))
                .map(inv -> {
                    // 查询该物料在该仓库的首次入库时间
                    LocalDateTime firstInTime = getFirstInTime(inv.getWarehouse().getId(), inv.getMaterial().getId());
                    long days = firstInTime != null ? Duration.between(firstInTime, now).toDays() : 0;

                    if (minDays != null && days < minDays) {
                        return null;
                    }

                    return InventoryAgeDTO.builder()
                            .warehouseId(inv.getWarehouse().getId())
                            .warehouseName(inv.getWarehouse().getName())
                            .materialId(inv.getMaterial().getId())
                            .materialName(inv.getMaterial().getName())
                            .materialCode(inv.getMaterial().getCode())
                            .unit(inv.getMaterial().getUnit())
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

        List<InStock> inStocks = inStockRepository.findByInStockTimeBetween(start, end);

        Map<LocalDate, List<InStock>> groupByDate = inStocks.stream()
                .filter(is -> operatorId == null ||
                        (is.getOperator() != null && is.getOperator().getId().equals(operatorId)))
                .collect(Collectors.groupingBy(is -> is.getInStockTime().toLocalDate()));

        return groupByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<InStock> stocks = entry.getValue();

                    long orderCount = stocks.size();
                    BigDecimal totalQty = stocks.stream()
                            .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                                    s.getDetails().stream()
                                            .map(InStockDetail::getQuantity)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 简单计算效率：平均每单数量
                    BigDecimal efficiency = orderCount > 0 ?
                            totalQty.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP) :
                            BigDecimal.ZERO;

                    return EfficiencyDTO.builder()
                            .date(date)
                            .operator(stocks.get(0).getOperator() != null ?
                                    stocks.get(0).getOperator().getUsername() : "未知")
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

        List<OutStock> outStocks = outStockRepository.findByOutStockTimeBetween(start, end);

        Map<LocalDate, List<OutStock>> groupByDate = outStocks.stream()
                .filter(os -> operatorId == null ||
                        (os.getOperator() != null && os.getOperator().getId().equals(operatorId)))
                .collect(Collectors.groupingBy(os -> os.getOutStockTime().toLocalDate()));

        return groupByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<OutStock> stocks = entry.getValue();

                    long orderCount = stocks.size();
                    BigDecimal totalQty = stocks.stream()
                            .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                                    s.getDetails().stream()
                                            .map(OutStockDetail::getQuantity)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 简单计算效率：平均每单数量
                    BigDecimal efficiency = orderCount > 0 ?
                            totalQty.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP) :
                            BigDecimal.ZERO;

                    return EfficiencyDTO.builder()
                            .date(date)
                            .operator(stocks.get(0).getOperator() != null ?
                                    stocks.get(0).getOperator().getUsername() : "未知")
                            .orderCount(orderCount)
                            .totalQuantity(totalQty)
                            .efficiencyRate(efficiency)
                            .operationType("拣货")
                            .build();
                })
                .sorted(Comparator.comparing(EfficiencyDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckDiffDTO> getCheckDifferences(LocalDateTime startDate, LocalDateTime endDate, Long warehouseId) {
        // 模拟盘点差异数据（实际项目中需要盘点单实体支持）
        // 这里演示基于当前库存生成模拟差异数据
        List<Inventory> inventories = inventoryRepository.findAll();
        Random random = new Random();

        return inventories.stream()
                .filter(inv -> warehouseId == null || inv.getWarehouse().getId().equals(warehouseId))
                .map(inv -> {
                    // 模拟差异率在-10%到10%之间
                    BigDecimal diffRate = BigDecimal.valueOf(random.nextDouble() * 0.2 - 0.1);
                    BigDecimal actualQty = inv.getQuantity().multiply(BigDecimal.ONE.add(diffRate))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal diffQty = actualQty.subtract(inv.getQuantity());

                    // 只返回有差异的记录
                    if (diffQty.abs().compareTo(BigDecimal.ZERO) == 0) {
                        return null;
                    }

                    return CheckDiffDTO.builder()
                            .checkNo("CHECK" + System.currentTimeMillis())
                            .checkTime(LocalDateTime.now())
                            .warehouseName(inv.getWarehouse().getName())
                            .materialName(inv.getMaterial().getName())
                            .materialCode(inv.getMaterial().getCode())
                            .unit(inv.getMaterial().getUnit())
                            .systemQty(inv.getQuantity())
                            .actualQty(actualQty)
                            .diffQty(diffQty)
                            .diffAmount(diffQty.multiply(inv.getUnitPrice()))
                            .diffReason(diffQty.compareTo(BigDecimal.ZERO) > 0 ? "盘盈" : "盘亏")
                            .checker("系统管理员")
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDistributionDTO> getInventoryDistributionByWarehouse() {
        List<Inventory> inventories = inventoryRepository.findAll();

        Map<Long, List<Inventory>> groupByWarehouse = inventories.stream()
                .collect(Collectors.groupingBy(inv -> inv.getWarehouse().getId()));

        BigDecimal totalAmount = inventories.stream()
                .map(Inventory::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return groupByWarehouse.entrySet().stream()
                .map(entry -> {
                    List<Inventory> warehouseInv = entry.getValue();
                    BigDecimal totalQty = warehouseInv.stream()
                            .map(Inventory::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal amount = warehouseInv.stream()
                            .map(Inventory::getTotalPrice)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return InventoryDistributionDTO.builder()
                            .warehouseId(entry.getKey())
                            .warehouseName(warehouseInv.get(0).getWarehouse().getName())
                            .quantity(totalQty)
                            .amount(amount)
                            .percentage(totalAmount.compareTo(BigDecimal.ZERO) > 0 ?
                                    amount.multiply(BigDecimal.valueOf(100))
                                            .divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP) :
                                    BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDistributionDTO> getInventoryDistributionByCategory() {
        List<Inventory> inventories = inventoryRepository.findAll();

        Map<Long, List<Inventory>> groupByCategory = inventories.stream()
                .filter(inv -> inv.getMaterial().getCategory() != null)
                .collect(Collectors.groupingBy(inv -> inv.getMaterial().getCategory().getId()));

        BigDecimal totalAmount = inventories.stream()
                .map(Inventory::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return groupByCategory.entrySet().stream()
                .map(entry -> {
                    List<Inventory> categoryInv = entry.getValue();
                    BigDecimal totalQty = categoryInv.stream()
                            .map(Inventory::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal amount = categoryInv.stream()
                            .map(Inventory::getTotalPrice)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return InventoryDistributionDTO.builder()
                            .categoryId(entry.getKey())
                            .categoryName(categoryInv.get(0).getMaterial().getCategory().getName())
                            .quantity(totalQty)
                            .amount(amount)
                            .percentage(totalAmount.compareTo(BigDecimal.ZERO) > 0 ?
                                    amount.multiply(BigDecimal.valueOf(100))
                                            .divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP) :
                                    BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InOutTrendDTO> getInOutTrend(LocalDate startDate, LocalDate endDate, Long warehouseId) {
        List<InOutTrendDTO> result = new ArrayList<>();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        // 查询入库数据
        List<InStock> inStocks = inStockRepository.findByInStockTimeBetween(start, end);
        Map<LocalDate, List<InStock>> inGroupByDate = inStocks.stream()
                .filter(is -> warehouseId == null || is.getWarehouse().getId().equals(warehouseId))
                .collect(Collectors.groupingBy(is -> is.getInStockTime().toLocalDate()));

        // 查询出库数据
        List<OutStock> outStocks = outStockRepository.findByOutStockTimeBetween(start, end);
        Map<LocalDate, List<OutStock>> outGroupByDate = outStocks.stream()
                .filter(os -> warehouseId == null || os.getWarehouse().getId().equals(warehouseId))
                .collect(Collectors.groupingBy(os -> os.getOutStockTime().toLocalDate()));

        // 生成日期范围内的所有日期数据
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<InStock> dayIn = inGroupByDate.getOrDefault(date, Collections.emptyList());
            List<OutStock> dayOut = outGroupByDate.getOrDefault(date, Collections.emptyList());

            BigDecimal inQty = dayIn.stream()
                    .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                            s.getDetails().stream()
                                    .map(InStockDetail::getQuantity)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal inAmt = dayIn.stream()
                    .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                            s.getDetails().stream()
                                    .map(InStockDetail::getTotalPrice)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal outQty = dayOut.stream()
                    .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                            s.getDetails().stream()
                                    .map(OutStockDetail::getQuantity)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal outAmt = dayOut.stream()
                    .map(s -> s.getDetails() == null ? BigDecimal.ZERO :
                            s.getDetails().stream()
                                    .map(OutStockDetail::getTotalPrice)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(InOutTrendDTO.builder()
                    .date(date)
                    .inQuantity(inQty)
                    .inAmount(inAmt)
                    .outQuantity(outQty)
                    .outAmount(outAmt)
                    .netQuantity(inQty.subtract(outQty))
                    .build());
        }

        return result;
    }

    // ==================== 辅助方法 ====================

    private InventorySummaryDTO convertToInventorySummary(Inventory inventory) {
        return InventorySummaryDTO.builder()
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .materialId(inventory.getMaterial().getId())
                .materialName(inventory.getMaterial().getName())
                .materialCode(inventory.getMaterial().getCode())
                .categoryName(inventory.getMaterial().getCategory() != null ?
                        inventory.getMaterial().getCategory().getName() : "未分类")
                .unit(inventory.getMaterial().getUnit())
                .quantity(inventory.getQuantity())
                .unitPrice(inventory.getUnitPrice())
                .totalPrice(inventory.getTotalPrice())
                .build();
    }

    private StockTransactionDTO convertToTransactionDTO(InStock inStock, InStockDetail detail, String type) {
        return StockTransactionDTO.builder()
                .orderNo(inStock.getOrderNo())
                .transactionType("入库")
                .transactionTime(inStock.getInStockTime())
                .warehouseName(inStock.getWarehouse().getName())
                .materialName(detail.getMaterial().getName())
                .materialCode(detail.getMaterial().getCode())
                .unit(detail.getMaterial().getUnit())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .totalPrice(detail.getTotalPrice())
                .operator(inStock.getOperator() != null ? inStock.getOperator().getUsername() : "未知")
                .remark(detail.getRemark())
                .build();
    }

    private StockTransactionDTO convertToTransactionDTO(OutStock outStock, OutStockDetail detail, String type) {
        return StockTransactionDTO.builder()
                .orderNo(outStock.getOrderNo())
                .transactionType("出库")
                .transactionTime(outStock.getOutStockTime())
                .warehouseName(outStock.getWarehouse().getName())
                .materialName(detail.getMaterial().getName())
                .materialCode(detail.getMaterial().getCode())
                .unit(detail.getMaterial().getUnit())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .totalPrice(detail.getTotalPrice())
                .operator(outStock.getOperator() != null ? outStock.getOperator().getUsername() : "未知")
                .remark(detail.getRemark())
                .build();
    }

    private LocalDateTime getFirstInTime(Long warehouseId, Long materialId) {
        // 查询最早的入库时间
        List<InStock> inStocks = inStockRepository.findAll();
        return inStocks.stream()
                .filter(is -> is.getWarehouse().getId().equals(warehouseId))
                .filter(is -> is.getDetails() != null && is.getDetails().stream()
                        .anyMatch(d -> d.getMaterial().getId().equals(materialId)))
                .map(InStock::getInStockTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private String getAgeLevel(long days) {
        if (days <= 30) {
            return "0-30天";
        } else if (days <= 90) {
            return "30-90天";
        } else if (days <= 180) {
            return "90-180天";
        } else {
            return "180天以上";
        }
    }
}
