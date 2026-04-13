package com.andy.warehouse.service;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.dto.report.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    PageResult<StockSummaryReport> getStockSummaryReport(Long warehouseId, Long categoryId, String keyword, Integer pageNum, Integer pageSize);

    PageResult<StockInSummaryReport> getStockInSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize);

    PageResult<StockOutSummaryReport> getStockOutSummaryReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize);

    PageResult<StockTransactionDetail> getStockTransactionDetail(Long warehouseId, Long materialId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize);

    PageResult<StockAgeAnalysis> getStockAgeAnalysis(Long warehouseId, Integer daysThreshold, Integer pageNum, Integer pageSize);

    PageResult<PickingEfficiencyReport> getPickingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize);

    PageResult<ShelvingEfficiencyReport> getShelvingEfficiencyReport(Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Integer pageNum, Integer pageSize);

    PageResult<InventoryVarianceReport> getInventoryVarianceReport(Long warehouseId, Integer pageNum, Integer pageSize);

    DashboardVO getDashboard();

    List<StockDistributionVO> getStockDistribution();

    List<StockTrendVO> getStockTrend(LocalDateTime startDate, LocalDateTime endDate);
}
