package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.mapper.ReportMapper;
import com.andy.warehouse.service.ReportService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public PageResult<InventorySummaryDTO> getInventorySummary(ReportQueryRequest request) {
        Page<ReportMapper.InventorySummaryProjection> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<ReportMapper.InventorySummaryProjection> resultPage = reportMapper.getInventorySummary(
                page,
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getMaterialCode(),
                request.getMaterialName()
        );

        List<InventorySummaryDTO> list = resultPage.getRecords().stream()
                .map(p -> InventorySummaryDTO.builder()
                        .warehouseId(p.getWarehouseId())
                        .warehouseName(p.getWarehouseName())
                        .materialId(p.getMaterialId())
                        .materialCode(p.getMaterialCode())
                        .materialName(p.getMaterialName())
                        .specification("")
                        .categoryName(p.getCategoryName())
                        .unit(p.getUnit())
                        .quantity(p.getQuantity())
                        .availableQuantity(p.getAvailableQuantity())
                        .lockedQuantity(p.getLockedQuantity())
                        .unitPrice(p.getUnitPrice())
                        .totalAmount(p.getTotalAmount())
                        .safetyStock(p.getSafetyStock())
                        .stockStatus(getStockStatus(p.getQuantity(), p.getSafetyStock()))
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public PageResult<StockInOutSummaryDTO> getStockInSummary(ReportQueryRequest request) {
        Page<ReportMapper.StockInSummaryProjection> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<ReportMapper.StockInSummaryProjection> resultPage = reportMapper.getStockInSummary(
                page,
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getStartDate(),
                request.getEndDate()
        );

        List<StockInOutSummaryDTO> list = resultPage.getRecords().stream()
                .map(p -> StockInOutSummaryDTO.builder()
                        .period(p.getPeriod())
                        .warehouseId(p.getWarehouseId())
                        .warehouseName(p.getWarehouseName())
                        .materialId(p.getMaterialId())
                        .materialCode(p.getMaterialCode())
                        .materialName(p.getMaterialName())
                        .categoryName(p.getCategoryName())
                        .beginQuantity(BigDecimal.ZERO)
                        .stockInQuantity(p.getStockInQuantity())
                        .stockOutQuantity(BigDecimal.ZERO)
                        .endQuantity(BigDecimal.ZERO)
                        .beginAmount(BigDecimal.ZERO)
                        .stockInAmount(p.getStockInAmount())
                        .stockOutAmount(BigDecimal.ZERO)
                        .endAmount(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public PageResult<StockInOutSummaryDTO> getStockOutSummary(ReportQueryRequest request) {
        Page<ReportMapper.StockOutSummaryProjection> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<ReportMapper.StockOutSummaryProjection> resultPage = reportMapper.getStockOutSummary(
                page,
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getStartDate(),
                request.getEndDate()
        );

        List<StockInOutSummaryDTO> list = resultPage.getRecords().stream()
                .map(p -> StockInOutSummaryDTO.builder()
                        .period(p.getPeriod())
                        .warehouseId(p.getWarehouseId())
                        .warehouseName(p.getWarehouseName())
                        .materialId(p.getMaterialId())
                        .materialCode(p.getMaterialCode())
                        .materialName(p.getMaterialName())
                        .categoryName(p.getCategoryName())
                        .beginQuantity(BigDecimal.ZERO)
                        .stockInQuantity(BigDecimal.ZERO)
                        .stockOutQuantity(p.getStockOutQuantity())
                        .endQuantity(BigDecimal.ZERO)
                        .beginAmount(BigDecimal.ZERO)
                        .stockInAmount(BigDecimal.ZERO)
                        .stockOutAmount(p.getStockOutAmount())
                        .endAmount(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public PageResult<InventoryDetailDTO> getInventoryDetail(ReportQueryRequest request) {
        Page<ReportMapper.InventoryDetailProjection> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<ReportMapper.InventoryDetailProjection> resultPage = reportMapper.getInventoryDetail(
                page,
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPeriodType()
        );

        List<InventoryDetailDTO> list = resultPage.getRecords().stream()
                .map(p -> InventoryDetailDTO.builder()
                        .recordNo(p.getRecordNo())
                        .recordType(p.getRecordType())
                        .bizType(p.getBizType())
                        .bizNo(p.getBizNo())
                        .recordTime(p.getRecordTime())
                        .materialId(p.getMaterialId())
                        .materialCode(p.getMaterialCode())
                        .materialName(p.getMaterialName())
                        .warehouseId(p.getWarehouseId())
                        .warehouseName(p.getWarehouseName())
                        .locationCode(p.getLocationCode())
                        .quantity(p.getQuantity())
                        .beforeQuantity(p.getBeforeQuantity())
                        .afterQuantity(p.getAfterQuantity())
                        .unit(p.getUnit())
                        .batchNo(p.getBatchNo())
                        .operatorName(p.getOperatorName())
                        .remark(p.getRemark())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public PageResult<InventoryAgeDTO> getInventoryAge(ReportQueryRequest request) {
        Page<ReportMapper.InventoryAgeProjection> page = new Page<>(request.getPageNum(), request.getPageSize());
        Integer maxAge = null;
        if (request.getPeriodType() != null) {
            try {
                maxAge = Integer.parseInt(request.getPeriodType());
            } catch (NumberFormatException ignored) {
            }
        }

        IPage<ReportMapper.InventoryAgeProjection> resultPage = reportMapper.getInventoryAge(
                page,
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                maxAge
        );

        List<InventoryAgeDTO> list = resultPage.getRecords().stream()
                .map(p -> InventoryAgeDTO.builder()
                        .warehouseId(p.getWarehouseId())
                        .warehouseName(p.getWarehouseName())
                        .materialId(p.getMaterialId())
                        .materialCode(p.getMaterialCode())
                        .materialName(p.getMaterialName())
                        .categoryName(p.getCategoryName())
                        .batchNo(p.getBatchNo())
                        .productionDate(p.getProductionDate())
                        .expiryDate(p.getExpiryDate())
                        .quantity(p.getQuantity())
                        .unit(p.getUnit())
                        .ageDays(p.getAgeDays())
                        .ageGroup(getAgeGroup(p.getAgeDays()))
                        .amount(p.getAmount())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public List<PickingEfficiencyDTO> getPickingEfficiency(ReportQueryRequest request) {
        return generateMockPickingEfficiency(request);
    }

    @Override
    public List<PutawayEfficiencyDTO> getPutawayEfficiency(ReportQueryRequest request) {
        return generateMockPutawayEfficiency(request);
    }

    @Override
    public List<InventoryCheckDiffDTO> getInventoryCheckDiff(ReportQueryRequest request) {
        return generateMockInventoryCheckDiff(request);
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate sevenDaysAgo = today.minusDays(6);

        BigDecimal totalInventoryQuantity = reportMapper.getTotalInventoryQuantity();
        BigDecimal totalInventoryAmount = reportMapper.getTotalInventoryAmount();
        Long lowStockCount = reportMapper.getLowStockMaterialCount();
        Long pendingStockInCount = reportMapper.getPendingStockInCount();
        Long pendingStockOutCount = reportMapper.getPendingStockOutCount();
        Long todayStockInCount = reportMapper.getTodayStockInCount();
        Long todayStockOutCount = reportMapper.getTodayStockOutCount();
        BigDecimal todayStockInQuantity = reportMapper.getTodayStockInQuantity();
        BigDecimal todayStockOutQuantity = reportMapper.getTodayStockOutQuantity();

        List<ReportMapper.WarehouseDistributionProjection> warehouseDist = reportMapper.getWarehouseDistribution();
        BigDecimal totalAmount = warehouseDist.stream()
                .map(ReportMapper.WarehouseDistributionProjection::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardSummaryDTO.WarehouseInventoryDTO> warehouseDistribution = warehouseDist.stream()
                .map(w -> DashboardSummaryDTO.WarehouseInventoryDTO.builder()
                        .warehouseId(w.getWarehouseId())
                        .warehouseName(w.getWarehouseName())
                        .quantity(w.getTotalQuantity())
                        .amount(w.getTotalAmount())
                        .percentage(totalAmount.compareTo(BigDecimal.ZERO) > 0
                                ? w.getTotalAmount().multiply(new BigDecimal("100")).divide(totalAmount, 2, RoundingMode.HALF_UP).doubleValue()
                                : 0.0)
                        .build())
                .collect(Collectors.toList());

        List<ReportMapper.TrendDataProjection> stockInTrendData = reportMapper.getStockInTrend(sevenDaysAgo, today);
        List<DashboardSummaryDTO.TrendDataDTO> stockInTrend = stockInTrendData.stream()
                .map(t -> DashboardSummaryDTO.TrendDataDTO.builder()
                        .date(t.getDate())
                        .quantity(t.getQuantity())
                        .amount(t.getAmount())
                        .build())
                .collect(Collectors.toList());

        List<ReportMapper.TrendDataProjection> stockOutTrendData = reportMapper.getStockOutTrend(sevenDaysAgo, today);
        List<DashboardSummaryDTO.TrendDataDTO> stockOutTrend = stockOutTrendData.stream()
                .map(t -> DashboardSummaryDTO.TrendDataDTO.builder()
                        .date(t.getDate())
                        .quantity(t.getQuantity())
                        .amount(t.getAmount())
                        .build())
                .collect(Collectors.toList());

        List<ReportMapper.CategoryDistributionProjection> categoryDist = reportMapper.getCategoryDistribution();
        BigDecimal categoryTotalAmount = categoryDist.stream()
                .map(ReportMapper.CategoryDistributionProjection::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardSummaryDTO.CategoryDistributionDTO> categoryDistribution = categoryDist.stream()
                .map(c -> DashboardSummaryDTO.CategoryDistributionDTO.builder()
                        .categoryName(c.getCategoryName() != null ? c.getCategoryName() : "未分类")
                        .amount(c.getAmount())
                        .percentage(categoryTotalAmount.compareTo(BigDecimal.ZERO) > 0
                                ? c.getAmount().multiply(new BigDecimal("100")).divide(categoryTotalAmount, 2, RoundingMode.HALF_UP).doubleValue()
                                : 0.0)
                        .build())
                .collect(Collectors.toList());

        return DashboardSummaryDTO.builder()
                .totalInventoryAmount(totalInventoryAmount)
                .totalInventoryQuantity(totalInventoryQuantity)
                .totalMaterialCount(categoryDist.size())
                .totalWarehouseCount(warehouseDist.size())
                .todayStockInQuantity(todayStockInQuantity)
                .todayStockOutQuantity(todayStockOutQuantity)
                .monthStockInAmount(BigDecimal.ZERO)
                .monthStockOutAmount(BigDecimal.ZERO)
                .lowStockCount(lowStockCount)
                .expiredCount(0L)
                .warehouseDistribution(warehouseDistribution)
                .stockInTrend(stockInTrend)
                .stockOutTrend(stockOutTrend)
                .categoryDistribution(categoryDistribution)
                .build();
    }

    private String getStockStatus(BigDecimal quantity, BigDecimal safetyStock) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            return "缺货";
        }
        if (safetyStock != null && quantity.compareTo(safetyStock) <= 0) {
            return "低于安全库存";
        }
        return "正常";
    }

    private String getAgeGroup(Integer ageDays) {
        if (ageDays == null) return "未知";
        if (ageDays <= 30) return "0-30天";
        if (ageDays <= 90) return "31-90天";
        if (ageDays <= 180) return "91-180天";
        if (ageDays <= 365) return "181-365天";
        return "365天以上";
    }

    private List<PickingEfficiencyDTO> generateMockPickingEfficiency(ReportQueryRequest request) {
        List<PickingEfficiencyDTO> list = new ArrayList<>();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(7);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            int orders = 20 + (int) (Math.random() * 30);
            int items = orders * 3 + (int) (Math.random() * 20);
            BigDecimal quantity = new BigDecimal(items * 5 + (int) (Math.random() * 50));
            BigDecimal timeMinutes = new BigDecimal(orders * 15 + (int) (Math.random() * 60));

            list.add(PickingEfficiencyDTO.builder()
                    .workDate(date)
                    .operatorId(1L)
                    .operatorName("张三")
                    .warehouseId(1L)
                    .warehouseName("主仓库")
                    .totalOrders(orders)
                    .totalItems(items)
                    .totalQuantity(quantity)
                    .totalTimeMinutes(timeMinutes)
                    .itemsPerHour(timeMinutes.compareTo(BigDecimal.ZERO) > 0
                            ? new BigDecimal(items).multiply(new BigDecimal("60")).divide(timeMinutes, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO)
                    .quantityPerHour(timeMinutes.compareTo(BigDecimal.ZERO) > 0
                            ? quantity.multiply(new BigDecimal("60")).divide(timeMinutes, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO)
                    .avgTimePerOrder(new BigDecimal(orders > 0 ? timeMinutes.doubleValue() / orders : 0).setScale(2, RoundingMode.HALF_UP))
                    .accuracyRate(new BigDecimal("98.5"))
                    .build());
        }
        return list;
    }

    private List<PutawayEfficiencyDTO> generateMockPutawayEfficiency(ReportQueryRequest request) {
        List<PutawayEfficiencyDTO> list = new ArrayList<>();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(7);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            int orders = 15 + (int) (Math.random() * 25);
            int items = orders * 4 + (int) (Math.random() * 30);
            BigDecimal quantity = new BigDecimal(items * 8 + (int) (Math.random() * 100));
            BigDecimal timeMinutes = new BigDecimal(orders * 20 + (int) (Math.random() * 80));

            list.add(PutawayEfficiencyDTO.builder()
                    .workDate(date)
                    .operatorId(1L)
                    .operatorName("李四")
                    .warehouseId(1L)
                    .warehouseName("主仓库")
                    .totalOrders(orders)
                    .totalItems(items)
                    .totalQuantity(quantity)
                    .totalTimeMinutes(timeMinutes)
                    .itemsPerHour(timeMinutes.compareTo(BigDecimal.ZERO) > 0
                            ? new BigDecimal(items).multiply(new BigDecimal("60")).divide(timeMinutes, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO)
                    .quantityPerHour(timeMinutes.compareTo(BigDecimal.ZERO) > 0
                            ? quantity.multiply(new BigDecimal("60")).divide(timeMinutes, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO)
                    .avgTimePerOrder(new BigDecimal(orders > 0 ? timeMinutes.doubleValue() / orders : 0).setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        return list;
    }

    private List<InventoryCheckDiffDTO> generateMockInventoryCheckDiff(ReportQueryRequest request) {
        List<InventoryCheckDiffDTO> list = new ArrayList<>();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();

        String[] materials = {"螺丝钉", "钢板", "电线", "开关", "插座"};
        String[] codes = {"MAT001", "MAT002", "MAT003", "MAT004", "MAT005"};

        for (int i = 0; i < 5; i++) {
            BigDecimal bookQty = new BigDecimal(100 + i * 50);
            BigDecimal actualQty = bookQty.add(new BigDecimal((int) (Math.random() * 10) - 5));
            BigDecimal diffQty = actualQty.subtract(bookQty);

            list.add(InventoryCheckDiffDTO.builder()
                    .checkNo("CHK" + System.currentTimeMillis() + i)
                    .checkDate(LocalDate.now().minusDays(i))
                    .warehouseId(1L)
                    .warehouseName("主仓库")
                    .materialId((long) (i + 1))
                    .materialCode(codes[i])
                    .materialName(materials[i])
                    .locationCode("A-01-0" + (i + 1))
                    .batchNo("BT2024" + (i + 1))
                    .bookQuantity(bookQty)
                    .actualQuantity(actualQty)
                    .diffQuantity(diffQty)
                    .diffAmount(diffQty.multiply(new BigDecimal("10.5")))
                    .status(diffQty.compareTo(BigDecimal.ZERO) == 0 ? "一致" : "差异")
                    .operatorId(1L)
                    .operatorName("王五")
                    .remark(diffQty.compareTo(BigDecimal.ZERO) != 0 ? "盘点差异" : "")
                    .build());
        }
        return list;
    }
}
