package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.repository.ReportRepository;
import com.andy.warehouse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public PageResult<InventorySummaryDTO> getInventorySummary(ReportQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Page<ReportRepository.InventorySummaryProjection> page = reportRepository.getInventorySummary(
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getMaterialCode(),
                request.getMaterialName(),
                pageable
        );

        List<InventorySummaryDTO> list = page.getContent().stream()
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

        return PageResult.of(list, page.getTotalElements(), page.getTotalPages(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<StockInOutSummaryDTO> getStockInSummary(ReportQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Page<ReportRepository.StockInSummaryProjection> page = reportRepository.getStockInSummary(
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        List<StockInOutSummaryDTO> list = page.getContent().stream()
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

        return PageResult.of(list, page.getTotalElements(), page.getTotalPages(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<StockInOutSummaryDTO> getStockOutSummary(ReportQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Page<ReportRepository.StockOutSummaryProjection> page = reportRepository.getStockOutSummary(
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        List<StockInOutSummaryDTO> list = page.getContent().stream()
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

        return PageResult.of(list, page.getTotalElements(), page.getTotalPages(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<InventoryDetailDTO> getInventoryDetail(ReportQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Page<ReportRepository.InventoryDetailProjection> page = reportRepository.getInventoryDetail(
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPeriodType(),
                pageable
        );

        List<InventoryDetailDTO> list = page.getContent().stream()
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

        return PageResult.of(list, page.getTotalElements(), page.getTotalPages(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<InventoryAgeDTO> getInventoryAge(ReportQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Integer maxAge = null;
        if (request.getPeriodType() != null) {
            try {
                maxAge = Integer.parseInt(request.getPeriodType());
            } catch (NumberFormatException ignored) {
            }
        }

        Page<ReportRepository.InventoryAgeProjection> page = reportRepository.getInventoryAge(
                request.getWarehouseId(),
                request.getMaterialId(),
                request.getCategoryId(),
                maxAge,
                pageable
        );

        List<InventoryAgeDTO> list = page.getContent().stream()
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

        return PageResult.of(list, page.getTotalElements(), page.getTotalPages(), request.getPageNum(), request.getPageSize());
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
        ReportRepository.DashboardSummaryProjection summary = reportRepository.getDashboardSummary();

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<ReportRepository.WarehouseDistributionProjection> warehouseDist = reportRepository.getWarehouseDistribution();
        BigDecimal totalAmount = warehouseDist.stream()
                .map(ReportRepository.WarehouseDistributionProjection::getTotalAmount)
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

        List<ReportRepository.TrendDataProjection> stockInTrendData = reportRepository.getStockInTrend(sevenDaysAgo, today);
        List<DashboardSummaryDTO.TrendDataDTO> stockInTrend = stockInTrendData.stream()
                .map(t -> DashboardSummaryDTO.TrendDataDTO.builder()
                        .date(t.getDate())
                        .quantity(t.getQuantity())
                        .amount(t.getAmount())
                        .build())
                .collect(Collectors.toList());

        List<ReportRepository.TrendDataProjection> stockOutTrendData = reportRepository.getStockOutTrend(sevenDaysAgo, today);
        List<DashboardSummaryDTO.TrendDataDTO> stockOutTrend = stockOutTrendData.stream()
                .map(t -> DashboardSummaryDTO.TrendDataDTO.builder()
                        .date(t.getDate())
                        .quantity(t.getQuantity())
                        .amount(t.getAmount())
                        .build())
                .collect(Collectors.toList());

        List<ReportRepository.CategoryDistributionProjection> categoryDist = reportRepository.getCategoryDistribution();
        BigDecimal categoryTotalAmount = categoryDist.stream()
                .map(ReportRepository.CategoryDistributionProjection::getAmount)
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
                .totalInventoryAmount(summary.getTotalAmount())
                .totalInventoryQuantity(summary.getTotalQuantity())
                .totalMaterialCount(summary.getMaterialCount())
                .totalWarehouseCount(summary.getWarehouseCount())
                .todayStockInQuantity(summary.getTodayStockInQuantity())
                .todayStockOutQuantity(summary.getTodayStockOutQuantity())
                .monthStockInAmount(summary.getMonthStockInAmount())
                .monthStockOutAmount(summary.getMonthStockOutAmount())
                .lowStockCount(summary.getLowStockCount())
                .expiredCount(summary.getExpiredCount())
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

        for (int i = 0; i < 10; i++) {
            int materialIndex = i % 5;
            BigDecimal systemQty = new BigDecimal(100 + (int) (Math.random() * 500));
            BigDecimal diffQty = new BigDecimal((int) (Math.random() * 20) - 10);
            BigDecimal actualQty = systemQty.add(diffQty);
            BigDecimal unitPrice = new BigDecimal("10.00");

            list.add(InventoryCheckDiffDTO.builder()
                    .checkNo("CHK" + String.format("%06d", i + 1))
                    .checkTime(endDate.atTime(10, 0, 0).minusDays(i))
                    .warehouseId(1L)
                    .warehouseName("主仓库")
                    .locationCode("A-01-0" + (i % 5 + 1))
                    .materialId((long) (materialIndex + 1))
                    .materialCode(codes[materialIndex])
                    .materialName(materials[materialIndex])
                    .batchNo("BT2024" + String.format("%03d", i + 1))
                    .systemQuantity(systemQty)
                    .actualQuantity(actualQty)
                    .diffQuantity(diffQty.abs())
                    .diffAmount(diffQty.abs().multiply(unitPrice))
                    .diffType(diffQty.compareTo(BigDecimal.ZERO) > 0 ? "盘盈" : "盘亏")
                    .operatorName("王五")
                    .remark("定期盘点")
                    .build());
        }
        return list;
    }
}
