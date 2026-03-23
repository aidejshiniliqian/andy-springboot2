package com.andy.warehouse.controller;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.common.Result;
import com.andy.warehouse.dto.report.*;
import com.andy.warehouse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/inventory/summary")
    public Result<PageResult<InventorySummaryDTO>> getInventorySummary(ReportQueryRequest request) {
        return Result.success(reportService.getInventorySummary(request));
    }

    @GetMapping("/stock-in/summary")
    public Result<PageResult<StockInOutSummaryDTO>> getStockInSummary(ReportQueryRequest request) {
        return Result.success(reportService.getStockInSummary(request));
    }

    @GetMapping("/stock-out/summary")
    public Result<PageResult<StockInOutSummaryDTO>> getStockOutSummary(ReportQueryRequest request) {
        return Result.success(reportService.getStockOutSummary(request));
    }

    @GetMapping("/inventory/detail")
    public Result<PageResult<InventoryDetailDTO>> getInventoryDetail(ReportQueryRequest request) {
        return Result.success(reportService.getInventoryDetail(request));
    }

    @GetMapping("/inventory/age")
    public Result<PageResult<InventoryAgeDTO>> getInventoryAge(ReportQueryRequest request) {
        return Result.success(reportService.getInventoryAge(request));
    }

    @GetMapping("/efficiency/picking")
    public Result<List<PickingEfficiencyDTO>> getPickingEfficiency(ReportQueryRequest request) {
        return Result.success(reportService.getPickingEfficiency(request));
    }

    @GetMapping("/efficiency/putaway")
    public Result<List<PutawayEfficiencyDTO>> getPutawayEfficiency(ReportQueryRequest request) {
        return Result.success(reportService.getPutawayEfficiency(request));
    }

    @GetMapping("/inventory-check/diff")
    public Result<List<InventoryCheckDiffDTO>> getInventoryCheckDiff(ReportQueryRequest request) {
        return Result.success(reportService.getInventoryCheckDiff(request));
    }

    @GetMapping("/dashboard/summary")
    public Result<DashboardSummaryDTO> getDashboardSummary() {
        return Result.success(reportService.getDashboardSummary());
    }
}
