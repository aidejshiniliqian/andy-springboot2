package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.repository.*;
import com.andy.warehouse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StockRepository stockRepository;
    private final StockInOrderRepository stockInOrderRepository;
    private final StockOutOrderRepository stockOutOrderRepository;
    private final StockInOrderItemRepository stockInOrderItemRepository;
    private final StockOutOrderItemRepository stockOutOrderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final InventoryCheckItemRepository inventoryCheckItemRepository;

    @Override
    public PageResult<StockSummaryReport> getStockSummaryReport(Long warehouseId, Long categoryId, String keyword, Pageable pageable) {
        List<Stock> stocks = stockRepository.findAllWithMaterialAndCategory();
        
        Map<Long, StockSummaryReport> reportMap = new LinkedHashMap<>();
        
        for (Stock stock : stocks) {
            if (warehouseId != null && !stock.getWarehouse().getId().equals(warehouseId)) {
                continue;
            }
            
            Material material = stock.getMaterial();
            if (categoryId != null && (material.getCategory() == null || !material.getCategory().getId().equals(categoryId))) {
                continue;
            }
            
            if (keyword != null && !keyword.isEmpty()) {
                if (!material.getName().contains(keyword) && 
                    (material.getCode() == null || !material.getCode().contains(keyword))) {
                    continue;
                }
            }
            
            Long materialId = material.getId();
            StockSummaryReport report = reportMap.get(materialId);
            
            if (report == null) {
                report = StockSummaryReport.builder()
                        .materialId(materialId)
                        .materialCode(material.getCode())
                        .materialName(material.getName())
                        .specification(material.getSpecification())
                        .unit(material.getUnit())
                        .categoryName(material.getCategory() != null ? material.getCategory().getName() : null)
                        .totalQuantity(0)
                        .availableQuantity(0)
                        .lockedQuantity(0)
                        .unitPrice(material.getPrice())
                        .safetyStock(material.getSafetyStock())
                        .build();
                reportMap.put(materialId, report);
            }
            
            report.setTotalQuantity(report.getTotalQuantity() + stock.getQuantity());
            report.setAvailableQuantity(report.getAvailableQuantity() + stock.getAvailableQuantity());
            report.setLockedQuantity(report.getLockedQuantity() + stock.getLockedQuantity());
        }
        
        List<StockSummaryReport> reports = new ArrayList<>(reportMap.values());
        
        for (StockSummaryReport report : reports) {
            if (report.getUnitPrice() != null && report.getTotalQuantity() != null) {
                report.setTotalAmount(report.getUnitPrice().multiply(BigDecimal.valueOf(report.getTotalQuantity())));
            }
            
            String stockStatus = "正常";
            if (report.getSafetyStock() != null && report.getTotalQuantity() < report.getSafetyStock()) {
                stockStatus = "库存不足";
            } else if (report.getTotalQuantity() == 0) {
                stockStatus = "缺货";
            }
            report.setStockStatus(stockStatus);
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reports.size());
        List<StockSummaryReport> pagedReports = reports.subList(start, end);
        
        return PageResult.of(pagedReports, (long) reports.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public PageResult<StockInSummaryReport> getStockInSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockInOrder> orderPage = stockInOrderRepository.searchOrders(warehouseId, startDate, endDate, null, pageable);
        
        List<StockInSummaryReport> reports = orderPage.getContent().stream()
                .map(this::convertToStockInSummaryReport)
                .collect(Collectors.toList());
        
        return PageResult.of(reports, orderPage.getTotalElements(), orderPage.getNumber() + 1, orderPage.getSize());
    }

    private StockInSummaryReport convertToStockInSummaryReport(StockInOrder order) {
        List<StockInOrderItem> items = stockInOrderItemRepository.findByOrderId(order.getId());
        int totalQuantity = items.stream().mapToInt(StockInOrderItem::getQuantity).sum();
        
        return StockInSummaryReport.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType())
                .orderTypeName(getStockInOrderTypeName(order.getOrderType()))
                .warehouseName(order.getWarehouse().getName())
                .supplier(order.getSupplier())
                .itemCount(items.size())
                .totalQuantity(totalQuantity)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .statusName(getOrderStatusName(order.getStatus()))
                .operatorName(order.getOperatorName())
                .build();
    }

    @Override
    public PageResult<StockOutSummaryReport> getStockOutSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockOutOrder> orderPage = stockOutOrderRepository.searchOrders(warehouseId, startDate, endDate, null, pageable);
        
        List<StockOutSummaryReport> reports = orderPage.getContent().stream()
                .map(this::convertToStockOutSummaryReport)
                .collect(Collectors.toList());
        
        return PageResult.of(reports, orderPage.getTotalElements(), orderPage.getNumber() + 1, orderPage.getSize());
    }

    private StockOutSummaryReport convertToStockOutSummaryReport(StockOutOrder order) {
        List<StockOutOrderItem> items = stockOutOrderItemRepository.findByOrderId(order.getId());
        int totalQuantity = items.stream().mapToInt(StockOutOrderItem::getQuantity).sum();
        
        return StockOutSummaryReport.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType())
                .orderTypeName(getStockOutOrderTypeName(order.getOrderType()))
                .warehouseName(order.getWarehouse().getName())
                .receiver(order.getReceiver())
                .itemCount(items.size())
                .totalQuantity(totalQuantity)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .statusName(getOrderStatusName(order.getStatus()))
                .operatorName(order.getOperatorName())
                .build();
    }

    @Override
    public PageResult<StockTransactionDetail> getStockTransactionDetail(Long warehouseId, Long materialId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<StockTransactionDetail> details = new ArrayList<>();
        
        List<StockInOrder> inOrders = stockInOrderRepository.findAll();
        for (StockInOrder order : inOrders) {
            if (order.getDeleted() || order.getStatus() != 1) continue;
            if (warehouseId != null && !order.getWarehouse().getId().equals(warehouseId)) continue;
            if (startDate != null && order.getOrderDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().isAfter(endDate)) continue;
            
            List<StockInOrderItem> items = stockInOrderItemRepository.findByOrderId(order.getId());
            for (StockInOrderItem item : items) {
                if (materialId != null && !item.getMaterial().getId().equals(materialId)) continue;
                
                details.add(StockTransactionDetail.builder()
                        .materialId(item.getMaterial().getId())
                        .materialCode(item.getMaterial().getCode())
                        .materialName(item.getMaterial().getName())
                        .specification(item.getMaterial().getSpecification())
                        .unit(item.getMaterial().getUnit())
                        .warehouseName(order.getWarehouse().getName())
                        .transactionType("入库")
                        .orderNo(order.getOrderNo())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .amount(item.getTotalPrice())
                        .batchNo(item.getBatchNo())
                        .transactionTime(order.getOrderDate())
                        .build());
            }
        }
        
        List<StockOutOrder> outOrders = stockOutOrderRepository.findAll();
        for (StockOutOrder order : outOrders) {
            if (order.getDeleted() || order.getStatus() != 1) continue;
            if (warehouseId != null && !order.getWarehouse().getId().equals(warehouseId)) continue;
            if (startDate != null && order.getOrderDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().isAfter(endDate)) continue;
            
            List<StockOutOrderItem> items = stockOutOrderItemRepository.findByOrderId(order.getId());
            for (StockOutOrderItem item : items) {
                if (materialId != null && !item.getMaterial().getId().equals(materialId)) continue;
                
                details.add(StockTransactionDetail.builder()
                        .materialId(item.getMaterial().getId())
                        .materialCode(item.getMaterial().getCode())
                        .materialName(item.getMaterial().getName())
                        .specification(item.getMaterial().getSpecification())
                        .unit(item.getMaterial().getUnit())
                        .warehouseName(order.getWarehouse().getName())
                        .transactionType("出库")
                        .orderNo(order.getOrderNo())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .amount(item.getTotalPrice())
                        .batchNo(item.getBatchNo())
                        .transactionTime(order.getOrderDate())
                        .build());
            }
        }
        
        details.sort((a, b) -> b.getTransactionTime().compareTo(a.getTransactionTime()));
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), details.size());
        List<StockTransactionDetail> pagedDetails = details.subList(start, end);
        
        return PageResult.of(pagedDetails, (long) details.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public PageResult<StockAgeAnalysis> getStockAgeAnalysis(Long warehouseId, Integer daysThreshold, Pageable pageable) {
        List<Stock> stocks;
        if (warehouseId != null) {
            stocks = stockRepository.findByWarehouseIdWithMaterialAndCategory(warehouseId);
        } else {
            stocks = stockRepository.findAllWithMaterialAndCategory();
        }
        
        LocalDateTime now = LocalDateTime.now();
        List<StockAgeAnalysis> analyses = new ArrayList<>();
        
        for (Stock stock : stocks) {
            if (stock.getQuantity() <= 0) continue;
            
            LocalDateTime inboundDate = stock.getCreatedAt();
            int stockAgeDays = (int) ChronoUnit.DAYS.between(inboundDate.toLocalDate(), now.toLocalDate());
            
            String ageRange = getAgeRange(stockAgeDays);
            
            if (daysThreshold != null && stockAgeDays < daysThreshold) {
                continue;
            }
            
            BigDecimal totalAmount = null;
            if (stock.getMaterial().getPrice() != null) {
                totalAmount = stock.getMaterial().getPrice().multiply(BigDecimal.valueOf(stock.getQuantity()));
            }
            
            analyses.add(StockAgeAnalysis.builder()
                    .materialId(stock.getMaterial().getId())
                    .materialCode(stock.getMaterial().getCode())
                    .materialName(stock.getMaterial().getName())
                    .specification(stock.getMaterial().getSpecification())
                    .unit(stock.getMaterial().getUnit())
                    .warehouseName(stock.getWarehouse().getName())
                    .batchNo(stock.getBatchNo())
                    .quantity(stock.getQuantity())
                    .unitPrice(stock.getMaterial().getPrice())
                    .totalAmount(totalAmount)
                    .inboundDate(inboundDate)
                    .stockAgeDays(stockAgeDays)
                    .ageRange(ageRange)
                    .position(stock.getPosition())
                    .build());
        }
        
        analyses.sort((a, b) -> b.getStockAgeDays().compareTo(a.getStockAgeDays()));
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), analyses.size());
        List<StockAgeAnalysis> pagedAnalyses = analyses.subList(start, end);
        
        return PageResult.of(pagedAnalyses, (long) analyses.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    private String getAgeRange(int days) {
        if (days <= 30) return "0-30天";
        if (days <= 90) return "31-90天";
        if (days <= 180) return "91-180天";
        if (days <= 365) return "181-365天";
        return "超过365天";
    }

    @Override
    public PageResult<PickingEfficiencyReport> getPickingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockOutOrder> orderPage = stockOutOrderRepository.searchOrders(warehouseId, startDate, endDate, 1, pageable);
        
        List<PickingEfficiencyReport> reports = orderPage.getContent().stream()
                .map(order -> {
                    List<StockOutOrderItem> items = stockOutOrderItemRepository.findByOrderId(order.getId());
                    int totalItems = items.size();
                    int totalQuantity = items.stream().mapToInt(StockOutOrderItem::getQuantity).sum();
                    
                    LocalDateTime startTime = order.getCreatedAt();
                    LocalDateTime endTime = order.getUpdatedAt();
                    long durationMinutes = startTime != null && endTime != null 
                            ? ChronoUnit.MINUTES.between(startTime, endTime) : 0;
                    
                    BigDecimal efficiency = BigDecimal.ZERO;
                    if (durationMinutes > 0 && totalQuantity > 0) {
                        efficiency = BigDecimal.valueOf(totalQuantity)
                                .divide(BigDecimal.valueOf(durationMinutes), 2, RoundingMode.HALF_UP);
                    }
                    
                    return PickingEfficiencyReport.builder()
                            .orderId(order.getId())
                            .orderNo(order.getOrderNo())
                            .warehouseName(order.getWarehouse().getName())
                            .operatorName(order.getOperatorName())
                            .totalItems(totalItems)
                            .completedItems(totalItems)
                            .completionRate(BigDecimal.valueOf(100))
                            .totalQuantity(totalQuantity)
                            .startTime(startTime)
                            .endTime(endTime)
                            .durationMinutes(durationMinutes)
                            .efficiency(efficiency)
                            .build();
                })
                .collect(Collectors.toList());
        
        return PageResult.of(reports, orderPage.getTotalElements(), orderPage.getNumber() + 1, orderPage.getSize());
    }

    @Override
    public PageResult<ShelvingEfficiencyReport> getShelvingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockInOrder> orderPage = stockInOrderRepository.searchOrders(warehouseId, startDate, endDate, 1, pageable);
        
        List<ShelvingEfficiencyReport> reports = orderPage.getContent().stream()
                .map(order -> {
                    List<StockInOrderItem> items = stockInOrderItemRepository.findByOrderId(order.getId());
                    int totalItems = items.size();
                    int totalQuantity = items.stream().mapToInt(StockInOrderItem::getQuantity).sum();
                    
                    LocalDateTime startTime = order.getCreatedAt();
                    LocalDateTime endTime = order.getUpdatedAt();
                    long durationMinutes = startTime != null && endTime != null 
                            ? ChronoUnit.MINUTES.between(startTime, endTime) : 0;
                    
                    BigDecimal efficiency = BigDecimal.ZERO;
                    if (durationMinutes > 0 && totalQuantity > 0) {
                        efficiency = BigDecimal.valueOf(totalQuantity)
                                .divide(BigDecimal.valueOf(durationMinutes), 2, RoundingMode.HALF_UP);
                    }
                    
                    return ShelvingEfficiencyReport.builder()
                            .orderId(order.getId())
                            .orderNo(order.getOrderNo())
                            .warehouseName(order.getWarehouse().getName())
                            .operatorName(order.getOperatorName())
                            .totalItems(totalItems)
                            .completedItems(totalItems)
                            .completionRate(BigDecimal.valueOf(100))
                            .totalQuantity(totalQuantity)
                            .startTime(startTime)
                            .endTime(endTime)
                            .durationMinutes(durationMinutes)
                            .efficiency(efficiency)
                            .build();
                })
                .collect(Collectors.toList());
        
        return PageResult.of(reports, orderPage.getTotalElements(), orderPage.getNumber() + 1, orderPage.getSize());
    }

    @Override
    public PageResult<InventoryVarianceReport> getInventoryVarianceReport(Long warehouseId, Pageable pageable) {
        List<InventoryVarianceReport> reports = new ArrayList<>();
        
        List<InventoryCheck> checks = inventoryCheckRepository.findCompletedChecks();
        
        for (InventoryCheck check : checks) {
            if (warehouseId != null && !check.getWarehouse().getId().equals(warehouseId)) {
                continue;
            }
            
            List<InventoryCheckItem> varianceItems = inventoryCheckItemRepository.findVarianceItemsByCheckId(check.getId());
            
            for (InventoryCheckItem item : varianceItems) {
                BigDecimal varianceRate = BigDecimal.ZERO;
                if (item.getSystemQuantity() > 0) {
                    varianceRate = BigDecimal.valueOf(Math.abs(item.getVarianceQuantity()))
                            .divide(BigDecimal.valueOf(item.getSystemQuantity()), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }
                
                reports.add(InventoryVarianceReport.builder()
                        .materialId(item.getMaterial().getId())
                        .materialCode(item.getMaterial().getCode())
                        .materialName(item.getMaterial().getName())
                        .specification(item.getMaterial().getSpecification())
                        .unit(item.getMaterial().getUnit())
                        .warehouseName(check.getWarehouse().getName())
                        .position(item.getPosition())
                        .systemQuantity(item.getSystemQuantity())
                        .actualQuantity(item.getActualQuantity())
                        .varianceQuantity(item.getVarianceQuantity())
                        .varianceRate(varianceRate)
                        .unitPrice(item.getUnitPrice())
                        .varianceAmount(item.getVarianceAmount())
                        .varianceType(item.getVarianceType())
                        .build());
            }
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reports.size());
        List<InventoryVarianceReport> pagedReports = reports.subList(start, end);
        
        return PageResult.of(pagedReports, (long) reports.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public DashboardVO getDashboard() {
        List<Warehouse> warehouses = warehouseRepository.findAllActive();
        Integer totalMaterials = stockRepository.countDistinctMaterials();
        Integer totalQuantity = stockRepository.getTotalQuantity();
        BigDecimal totalAmount = stockRepository.getTotalAmount();
        
        List<Material> materialsForAlert = stockRepository.findMaterialsForStockAlert();
        List<DashboardVO.MaterialStockAlert> stockAlerts = new ArrayList<>();
        
        for (Material material : materialsForAlert) {
            Integer currentQuantity = stockRepository.getTotalQuantityByMaterialId(material.getId());
            if (currentQuantity < material.getSafetyStock()) {
                stockAlerts.add(DashboardVO.MaterialStockAlert.builder()
                        .materialId(material.getId())
                        .materialCode(material.getCode())
                        .materialName(material.getName())
                        .currentQuantity(currentQuantity)
                        .safetyStock(material.getSafetyStock())
                        .alertType(currentQuantity == 0 ? "缺货" : "库存不足")
                        .build());
            }
        }
        
        LocalDateTime today = LocalDateTime.now();
        Integer todayInOrders = stockInOrderRepository.countByOrderDate(today);
        Integer todayOutOrders = stockOutOrderRepository.countByOrderDate(today);
        Integer todayInQuantity = stockInOrderRepository.sumQuantityByOrderDate(today);
        Integer todayOutQuantity = stockOutOrderRepository.sumQuantityByOrderDate(today);
        BigDecimal todayInAmount = stockInOrderRepository.sumAmountByOrderDate(today);
        BigDecimal todayOutAmount = stockOutOrderRepository.sumAmountByOrderDate(today);
        
        return DashboardVO.builder()
                .stockOverview(DashboardVO.StockOverview.builder()
                        .totalMaterials(totalMaterials)
                        .totalQuantity(totalQuantity)
                        .totalAmount(totalAmount)
                        .warehouseCount(warehouses.size())
                        .alertCount(stockAlerts.size())
                        .build())
                .stockDistribution(getStockDistribution())
                .stockTrend(getStockTrend(LocalDateTime.now().minusDays(7), LocalDateTime.now()))
                .stockAlerts(stockAlerts)
                .todayStatistics(DashboardVO.TodayStatistics.builder()
                        .todayInOrders(todayInOrders != null ? todayInOrders : 0)
                        .todayOutOrders(todayOutOrders != null ? todayOutOrders : 0)
                        .todayInQuantity(todayInQuantity != null ? todayInQuantity : 0)
                        .todayOutQuantity(todayOutQuantity != null ? todayOutQuantity : 0)
                        .todayInAmount(todayInAmount != null ? todayInAmount : BigDecimal.ZERO)
                        .todayOutAmount(todayOutAmount != null ? todayOutAmount : BigDecimal.ZERO)
                        .build())
                .build();
    }

    @Override
    public List<StockDistributionVO> getStockDistribution() {
        List<Warehouse> warehouses = warehouseRepository.findAllActive();
        List<Object[]> distribution = stockRepository.getStockDistributionByWarehouse();
        
        Map<Long, Object[]> distributionMap = distribution.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> arr
                ));
        
        return warehouses.stream()
                .map(warehouse -> {
                    Object[] data = distributionMap.get(warehouse.getId());
                    Integer materialCount = 0;
                    Integer totalQuantity = 0;
                    
                    if (data != null) {
                        materialCount = ((Number) data[1]).intValue();
                        totalQuantity = ((Number) data[2]).intValue();
                    }
                    
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    List<Stock> stocks = stockRepository.findByWarehouseId(warehouse.getId());
                    for (Stock stock : stocks) {
                        if (stock.getMaterial().getPrice() != null) {
                            totalAmount = totalAmount.add(
                                    stock.getMaterial().getPrice().multiply(BigDecimal.valueOf(stock.getQuantity()))
                            );
                        }
                    }
                    
                    BigDecimal utilizationRate = BigDecimal.ZERO;
                    if (warehouse.getCapacity() != null && warehouse.getCapacity() > 0) {
                        utilizationRate = BigDecimal.valueOf(totalQuantity)
                                .divide(BigDecimal.valueOf(warehouse.getCapacity()), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                    }
                    
                    return StockDistributionVO.builder()
                            .warehouseId(warehouse.getId())
                            .warehouseName(warehouse.getName())
                            .materialCount(materialCount)
                            .totalQuantity(totalQuantity)
                            .totalAmount(totalAmount)
                            .utilizationRate(utilizationRate)
                            .capacity(warehouse.getCapacity())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StockTrendVO> getStockTrend(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> inStats = stockInOrderRepository.getDailyStatistics(startDate, endDate);
        List<Object[]> outStats = stockOutOrderRepository.getDailyStatistics(startDate, endDate);
        
        Map<LocalDate, Object[]> inStatsMap = inStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((java.sql.Date) arr[0]).toLocalDate(),
                        arr -> arr
                ));
        
        Map<LocalDate, Object[]> outStatsMap = outStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((java.sql.Date) arr[0]).toLocalDate(),
                        arr -> arr
                ));
        
        List<StockTrendVO> trends = new ArrayList<>();
        LocalDate current = startDate.toLocalDate();
        while (!current.isAfter(endDate.toLocalDate())) {
            Object[] inData = inStatsMap.get(current);
            Object[] outData = outStatsMap.get(current);
            
            Integer inQuantity = inData != null ? ((Number) inData[1]).intValue() : 0;
            BigDecimal inAmount = inData != null ? (BigDecimal) inData[2] : BigDecimal.ZERO;
            Integer outQuantity = outData != null ? ((Number) outData[1]).intValue() : 0;
            BigDecimal outAmount = outData != null ? (BigDecimal) outData[2] : BigDecimal.ZERO;
            
            trends.add(StockTrendVO.builder()
                    .date(current.toString())
                    .inQuantity(inQuantity)
                    .outQuantity(outQuantity)
                    .netQuantity(inQuantity - outQuantity)
                    .inAmount(inAmount != null ? inAmount : BigDecimal.ZERO)
                    .outAmount(outAmount != null ? outAmount : BigDecimal.ZERO)
                    .orderCount((inData != null ? 1 : 0) + (outData != null ? 1 : 0))
                    .build());
            
            current = current.plusDays(1);
        }
        
        return trends;
    }

    private String getStockInOrderTypeName(Integer orderType) {
        if (orderType == null) return "未知";
        switch (orderType) {
            case 1: return "采购入库";
            case 2: return "退货入库";
            case 3: return "调拨入库";
            case 4: return "盘点入库";
            default: return "其他";
        }
    }

    private String getStockOutOrderTypeName(Integer orderType) {
        if (orderType == null) return "未知";
        switch (orderType) {
            case 1: return "销售出库";
            case 2: return "领料出库";
            case 3: return "调拨出库";
            case 4: return "盘点出库";
            default: return "其他";
        }
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待处理";
            case 1: return "已完成";
            case 2: return "已取消";
            default: return "未知";
        }
    }
}
