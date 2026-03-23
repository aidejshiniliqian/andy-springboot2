package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.report.*;

import java.util.List;

public interface ReportService {

    PageResult<InventorySummaryDTO> getInventorySummary(ReportQueryRequest request);

    PageResult<StockInOutSummaryDTO> getStockInSummary(ReportQueryRequest request);

    PageResult<StockInOutSummaryDTO> getStockOutSummary(ReportQueryRequest request);

    PageResult<InventoryDetailDTO> getInventoryDetail(ReportQueryRequest request);

    PageResult<InventoryAgeDTO> getInventoryAge(ReportQueryRequest request);

    List<PickingEfficiencyDTO> getPickingEfficiency(ReportQueryRequest request);

    List<PutawayEfficiencyDTO> getPutawayEfficiency(ReportQueryRequest request);

    List<InventoryCheckDiffDTO> getInventoryCheckDiff(ReportQueryRequest request);

    DashboardSummaryDTO getDashboardSummary();
}
