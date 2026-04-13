package com.andy.warehouse.controller;

import com.andy.warehouse.common.PageResult;
import com.andy.warehouse.common.Result;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/stock/summary")
    public Result<PageResult<StockSummaryReport>> getStockSummaryReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<StockSummaryReport> result = reportService.getStockSummaryReport(
                warehouseId, categoryId, keyword, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/stock-in/summary")
    public Result<PageResult<StockInSummaryReport>> getStockInSummaryReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<StockInSummaryReport> result = reportService.getStockInSummaryReport(
                warehouseId, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/stock-out/summary")
    public Result<PageResult<StockOutSummaryReport>> getStockOutSummaryReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<StockOutSummaryReport> result = reportService.getStockOutSummaryReport(
                warehouseId, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/stock/transaction-detail")
    public Result<PageResult<StockTransactionDetail>> getStockTransactionDetail(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<StockTransactionDetail> result = reportService.getStockTransactionDetail(
                warehouseId, materialId, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/stock/age-analysis")
    public Result<PageResult<StockAgeAnalysis>> getStockAgeAnalysis(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer daysThreshold,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<StockAgeAnalysis> result = reportService.getStockAgeAnalysis(
                warehouseId, daysThreshold, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/efficiency/picking")
    public Result<PageResult<PickingEfficiencyReport>> getPickingEfficiencyReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<PickingEfficiencyReport> result = reportService.getPickingEfficiencyReport(
                warehouseId, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/efficiency/shelving")
    public Result<PageResult<ShelvingEfficiencyReport>> getShelvingEfficiencyReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<ShelvingEfficiencyReport> result = reportService.getShelvingEfficiencyReport(
                warehouseId, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/inventory/variance")
    public Result<PageResult<InventoryVarianceReport>> getInventoryVarianceReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<InventoryVarianceReport> result = reportService.getInventoryVarianceReport(
                warehouseId, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO dashboard = reportService.getDashboard();
        return Result.success(dashboard);
    }

    @GetMapping("/dashboard/stock-distribution")
    public Result<List<StockDistributionVO>> getStockDistribution() {
        List<StockDistributionVO> distribution = reportService.getStockDistribution();
        return Result.success(distribution);
    }

    @GetMapping("/dashboard/stock-trend")
    public Result<List<StockTrendVO>> getStockTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        List<StockTrendVO> trend = reportService.getStockTrend(startDate, endDate);
        return Result.success(trend);
    }
}
