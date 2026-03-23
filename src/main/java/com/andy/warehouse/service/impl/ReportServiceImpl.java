package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.entity.*;
import com.andy.warehouse.mapper.*;
import com.andy.warehouse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StockMapper stockMapper;
    private final StockInOrderMapper stockInOrderMapper;
    private final StockOutOrderMapper stockOutOrderMapper;
    private final StockInOrderItemMapper stockInOrderItemMapper;
    private final StockOutOrderItemMapper stockOutOrderItemMapper;
    private final WarehouseMapper warehouseMapper;
    private final MaterialMapper materialMapper;
    private final MaterialCategoryMapper materialCategoryMapper;

    @Override
    public PageResult<StockSummaryReport> getStockSummaryReport(Long warehouseId, Long categoryId, String keyword, Integer pageNum, Integer pageSize) {
        List<Stock> stocks = stockMapper.findAllWithMaterialAndCategory();
        
        Map<Long, StockSummaryReport> reportMap = new LinkedHashMap<>();
        
        for (Stock stock : stocks) {
            if (warehouseId != null && !stock.getWarehouseId().equals(warehouseId)) {
                continue;
            }
            
            Material material = stock.getMaterial();
            if (material == null) {
                material = materialMapper.selectById(stock.getMaterialId());
                stock.setMaterial(material);
            }
            if (material == null) continue;
            
            if (categoryId != null) {
                if (material.getCategoryId() == null || !material.getCategoryId().equals(categoryId)) {
                    continue;
                }
            }
            
            if (keyword != null && !keyword.isEmpty()) {
                if (!material.getName().contains(keyword) && 
                    (material.getCode() == null || !material.getCode().contains(keyword))) {
                    continue;
                }
            }
            
            if (material.getCategory() == null && material.getCategoryId() != null) {
                MaterialCategory category = materialCategoryMapper.selectById(material.getCategoryId());
                material.setCategory(category);
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
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), reports.size());
        List<StockSummaryReport> pagedReports = start < reports.size() ? reports.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedReports, (long) reports.size(), pageNum, pageSize);
    }

    @Override
    public PageResult<StockInSummaryReport> getStockInSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize) {
        List<StockInOrder> orders = stockInOrderMapper.searchOrders(warehouseId, startDate, endDate, null);
        
        List<StockInSummaryReport> reports = orders.stream()
                .map(this::convertToStockInSummaryReport)
                .collect(Collectors.toList());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), reports.size());
        List<StockInSummaryReport> pagedReports = start < reports.size() ? reports.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedReports, (long) reports.size(), pageNum, pageSize);
    }

    private StockInSummaryReport convertToStockInSummaryReport(StockInOrder order) {
        List<StockInOrderItem> items = stockInOrderItemMapper.findByOrderId(order.getId());
        int totalQuantity = items.stream().mapToInt(StockInOrderItem::getQuantity).sum();
        
        String warehouseName = null;
        if (order.getWarehouseId() != null) {
            Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
            warehouseName = warehouse != null ? warehouse.getName() : null;
        }
        
        return StockInSummaryReport.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType())
                .orderTypeName(getStockInOrderTypeName(order.getOrderType()))
                .warehouseName(warehouseName)
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
    public PageResult<StockOutSummaryReport> getStockOutSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize) {
        List<StockOutOrder> orders = stockOutOrderMapper.searchOrders(warehouseId, startDate, endDate, null);
        
        List<StockOutSummaryReport> reports = orders.stream()
                .map(this::convertToStockOutSummaryReport)
                .collect(Collectors.toList());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), reports.size());
        List<StockOutSummaryReport> pagedReports = start < reports.size() ? reports.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedReports, (long) reports.size(), pageNum, pageSize);
    }

    private StockOutSummaryReport convertToStockOutSummaryReport(StockOutOrder order) {
        List<StockOutOrderItem> items = stockOutOrderItemMapper.findByOrderId(order.getId());
        int totalQuantity = items.stream().mapToInt(StockOutOrderItem::getQuantity).sum();
        
        String warehouseName = null;
        if (order.getWarehouseId() != null) {
            Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
            warehouseName = warehouse != null ? warehouse.getName() : null;
        }
        
        return StockOutSummaryReport.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType())
                .orderTypeName(getStockOutOrderTypeName(order.getOrderType()))
                .warehouseName(warehouseName)
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
    public PageResult<StockTransactionDetail> getStockTransactionDetail(Long warehouseId, Long materialId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize) {
        List<StockTransactionDetail> details = new ArrayList<>();
        
        List<StockInOrder> inOrders = stockInOrderMapper.selectAll();
        for (StockInOrder order : inOrders) {
            if (order.getDeleted() || order.getStatus() != 1) continue;
            if (warehouseId != null && !order.getWarehouseId().equals(warehouseId)) continue;
            if (startDate != null && order.getOrderDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().isAfter(endDate)) continue;
            
            String warehouseName = null;
            if (order.getWarehouseId() != null) {
                Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
                warehouseName = warehouse != null ? warehouse.getName() : null;
            }
            
            List<StockInOrderItem> items = stockInOrderItemMapper.findByOrderId(order.getId());
            for (StockInOrderItem item : items) {
                if (materialId != null && !item.getMaterialId().equals(materialId)) continue;
                
                Material material = materialMapper.selectById(item.getMaterialId());
                if (material == null) continue;
                
                details.add(StockTransactionDetail.builder()
                        .materialId(material.getId())
                        .materialCode(material.getCode())
                        .materialName(material.getName())
                        .specification(material.getSpecification())
                        .unit(material.getUnit())
                        .warehouseName(warehouseName)
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
        
        List<StockOutOrder> outOrders = stockOutOrderMapper.selectAll();
        for (StockOutOrder order : outOrders) {
            if (order.getDeleted() || order.getStatus() != 1) continue;
            if (warehouseId != null && !order.getWarehouseId().equals(warehouseId)) continue;
            if (startDate != null && order.getOrderDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().isAfter(endDate)) continue;
            
            String warehouseName = null;
            if (order.getWarehouseId() != null) {
                Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
                warehouseName = warehouse != null ? warehouse.getName() : null;
            }
            
            List<StockOutOrderItem> items = stockOutOrderItemMapper.findByOrderId(order.getId());
            for (StockOutOrderItem item : items) {
                if (materialId != null && !item.getMaterialId().equals(materialId)) continue;
                
                Material material = materialMapper.selectById(item.getMaterialId());
                if (material == null) continue;
                
                details.add(StockTransactionDetail.builder()
                        .materialId(material.getId())
                        .materialCode(material.getCode())
                        .materialName(material.getName())
                        .specification(material.getSpecification())
                        .unit(material.getUnit())
                        .warehouseName(warehouseName)
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
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), details.size());
        List<StockTransactionDetail> pagedDetails = start < details.size() ? details.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedDetails, (long) details.size(), pageNum, pageSize);
    }

    @Override
    public PageResult<StockAgeAnalysis> getStockAgeAnalysis(Long warehouseId, Integer daysThreshold, Integer pageNum, Integer pageSize) {
        List<Stock> stocks;
        if (warehouseId != null) {
            stocks = stockMapper.findByWarehouseIdWithMaterial(warehouseId);
        } else {
            stocks = stockMapper.findAllWithMaterialAndCategory();
        }
        
        LocalDateTime now = LocalDateTime.now();
        List<StockAgeAnalysis> analyses = new ArrayList<>();
        
        for (Stock stock : stocks) {
            if (stock.getQuantity() <= 0) continue;
            
            Material material = stock.getMaterial();
            if (material == null) {
                material = materialMapper.selectById(stock.getMaterialId());
                stock.setMaterial(material);
            }
            if (material == null) continue;
            
            String warehouseName = null;
            if (stock.getWarehouseId() != null) {
                Warehouse warehouse = warehouseMapper.selectById(stock.getWarehouseId());
                warehouseName = warehouse != null ? warehouse.getName() : null;
            }
            
            LocalDateTime inboundDate = stock.getCreatedAt();
            int stockAgeDays = (int) ChronoUnit.DAYS.between(inboundDate.toLocalDate(), now.toLocalDate());
            
            String ageRange = getAgeRange(stockAgeDays);
            
            if (daysThreshold != null && stockAgeDays < daysThreshold) {
                continue;
            }
            
            BigDecimal totalAmount = null;
            if (material.getPrice() != null) {
                totalAmount = material.getPrice().multiply(BigDecimal.valueOf(stock.getQuantity()));
            }
            
            analyses.add(StockAgeAnalysis.builder()
                    .materialId(material.getId())
                    .materialCode(material.getCode())
                    .materialName(material.getName())
                    .specification(material.getSpecification())
                    .unit(material.getUnit())
                    .warehouseName(warehouseName)
                    .batchNo(stock.getBatchNo())
                    .quantity(stock.getQuantity())
                    .unitPrice(material.getPrice())
                    .totalAmount(totalAmount)
                    .inboundDate(inboundDate)
                    .stockAgeDays(stockAgeDays)
                    .ageRange(ageRange)
                    .position(stock.getPosition())
                    .build());
        }
        
        analyses.sort((a, b) -> b.getStockAgeDays().compareTo(a.getStockAgeDays()));
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), analyses.size());
        List<StockAgeAnalysis> pagedAnalyses = start < analyses.size() ? analyses.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedAnalyses, (long) analyses.size(), pageNum, pageSize);
    }

    private String getAgeRange(int days) {
        if (days <= 30) return "0-30天";
        if (days <= 90) return "31-90天";
        if (days <= 180) return "91-180天";
        if (days <= 365) return "181-365天";
        return "超过365天";
    }

    @Override
    public PageResult<PickingEfficiencyReport> getPickingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize) {
        List<StockOutOrder> orders = stockOutOrderMapper.searchOrders(warehouseId, startDate, endDate, 1);
        
        List<PickingEfficiencyReport> reports = orders.stream()
                .map(order -> {
                    List<StockOutOrderItem> items = stockOutOrderItemMapper.findByOrderId(order.getId());
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
                    
                    String warehouseName = null;
                    if (order.getWarehouseId() != null) {
                        Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
                        warehouseName = warehouse != null ? warehouse.getName() : null;
                    }
                    
                    return PickingEfficiencyReport.builder()
                            .orderId(order.getId())
                            .orderNo(order.getOrderNo())
                            .warehouseName(warehouseName)
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
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), reports.size());
        List<PickingEfficiencyReport> pagedReports = start < reports.size() ? reports.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedReports, (long) reports.size(), pageNum, pageSize);
    }

    @Override
    public PageResult<ShelvingEfficiencyReport> getShelvingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize) {
        List<StockInOrder> orders = stockInOrderMapper.searchOrders(warehouseId, startDate, endDate, 1);
        
        List<ShelvingEfficiencyReport> reports = orders.stream()
                .map(order -> {
                    List<StockInOrderItem> items = stockInOrderItemMapper.findByOrderId(order.getId());
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
                    
                    String warehouseName = null;
                    if (order.getWarehouseId() != null) {
                        Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
                        warehouseName = warehouse != null ? warehouse.getName() : null;
                    }
                    
                    return ShelvingEfficiencyReport.builder()
                            .orderId(order.getId())
                            .orderNo(order.getOrderNo())
                            .warehouseName(warehouseName)
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
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), reports.size());
        List<ShelvingEfficiencyReport> pagedReports = start < reports.size() ? reports.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(pagedReports, (long) reports.size(), pageNum, pageSize);
    }

    @Override
    public PageResult<InventoryVarianceReport> getInventoryVarianceReport(Long warehouseId, Integer pageNum, Integer pageSize) {
        return PageResult.of(new ArrayList<>(), 0L, pageNum, pageSize);
    }

    @Override
    public DashboardVO getDashboard() {
        Long totalMaterials = materialMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Material>()
                .eq(Material::getDeleted, false));
        
        Long totalWarehouses = warehouseMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getDeleted, false));
        
        Long pendingInOrders = stockInOrderMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockInOrder>()
                .eq(StockInOrder::getDeleted, false)
                .eq(StockInOrder::getStatus, 0));
        
        Long pendingOutOrders = stockOutOrderMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockOutOrder>()
                .eq(StockOutOrder::getDeleted, false)
                .eq(StockOutOrder::getStatus, 0));
        
        DashboardVO.StockOverview stockOverview = DashboardVO.StockOverview.builder()
                .totalMaterials(totalMaterials != null ? totalMaterials.intValue() : 0)
                .warehouseCount(totalWarehouses != null ? totalWarehouses.intValue() : 0)
                .build();
        
        DashboardVO.TodayStatistics todayStatistics = DashboardVO.TodayStatistics.builder()
                .todayInOrders(pendingInOrders != null ? pendingInOrders.intValue() : 0)
                .todayOutOrders(pendingOutOrders != null ? pendingOutOrders.intValue() : 0)
                .build();
        
        return DashboardVO.builder()
                .stockOverview(stockOverview)
                .todayStatistics(todayStatistics)
                .build();
    }

    @Override
    public List<StockDistributionVO> getStockDistribution() {
        List<Warehouse> warehouses = warehouseMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getDeleted, false));
        
        List<StockDistributionVO> distributions = new ArrayList<>();
        
        for (Warehouse warehouse : warehouses) {
            Long totalQuantity = stockMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Stock>()
                    .eq(Stock::getWarehouseId, warehouse.getId())
                    .eq(Stock::getDeleted, false));
            
            distributions.add(StockDistributionVO.builder()
                    .warehouseId(warehouse.getId())
                    .warehouseName(warehouse.getName())
                    .totalQuantity(totalQuantity != null ? totalQuantity.intValue() : 0)
                    .build());
        }
        
        return distributions;
    }

    @Override
    public List<StockTrendVO> getStockTrend(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockTrendVO> trends = new ArrayList<>();
        
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDateTime dayStart = current.withHour(0).withMinute(0).withSecond(0);
            final LocalDateTime dayEnd = current.withHour(23).withMinute(59).withSecond(59);
            
            Long inQuantity = stockInOrderMapper.getTotalQuantityByDateRange(dayStart, dayEnd);
            Long outQuantity = stockOutOrderMapper.getTotalQuantityByDateRange(dayStart, dayEnd);
            
            trends.add(StockTrendVO.builder()
                    .date(current.toLocalDate().toString())
                    .inQuantity(inQuantity != null ? inQuantity.intValue() : 0)
                    .outQuantity(outQuantity != null ? outQuantity.intValue() : 0)
                    .build());
            
            current = current.plusDays(1);
        }
        
        return trends;
    }

    private String getStockInOrderTypeName(Integer orderType) {
        if (orderType == null) return "其他";
        switch (orderType) {
            case 1: return "采购入库";
            case 2: return "退货入库";
            case 3: return "调拨入库";
            case 4: return "盘点入库";
            default: return "其他";
        }
    }

    private String getStockOutOrderTypeName(Integer orderType) {
        if (orderType == null) return "其他";
        switch (orderType) {
            case 1: return "销售出库";
            case 2: return "领用出库";
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
