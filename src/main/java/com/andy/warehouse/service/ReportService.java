package com.andy.warehouse.service;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.dto.report.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    PageResult<StockSummaryReport> getStockSummaryReport(Long warehouseId, Long categoryId, String keyword, Pageable pageable);

    PageResult<StockInSummaryReport> getStockInSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    PageResult<StockOutSummaryReport> getStockOutSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    PageResult<StockTransactionDetail> getStockTransactionDetail(Long warehouseId, Long materialId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    PageResult<StockAgeAnalysis> getStockAgeAnalysis(Long warehouseId, Integer daysThreshold, Pageable pageable);

    PageResult<PickingEfficiencyReport> getPickingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    PageResult<ShelvingEfficiencyReport> getShelvingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    PageResult<InventoryVarianceReport> getInventoryVarianceReport(Long warehouseId, Pageable pageable);

    DashboardVO getDashboard();

    List<StockDistributionVO> getStockDistribution();

    List<StockTrendVO> getStockTrend(LocalDateTime startDate, LocalDateTime endDate);
}
