package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.dto.*;
import com.warehouse.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表控制器
 * 提供库存报表、效率报表、可视化看板等API接口
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ==================== 库存汇总报表 ====================

    /**
     * 获取库存汇总报表
     * @param warehouseId 仓库ID（可选）
     * @param categoryId 分类ID（可选）
     * @param materialId 物料ID（可选）
     * @return 库存汇总列表
     */
    @GetMapping("/inventory-summary")
    public Result<List<InventorySummaryDTO>> getInventorySummary(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialId) {
        try {
            List<InventorySummaryDTO> result = reportService.getInventorySummary(warehouseId, categoryId, materialId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询库存汇总失败: " + e.getMessage());
        }
    }

    // ==================== 收发存明细表 ====================

    /**
     * 获取收发存明细报表
     * @param startDate 开始日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param endDate 结束日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param warehouseId 仓库ID（可选）
     * @param materialId 物料ID（可选）
     * @param transactionType 交易类型（可选：IN/OUT）
     * @return 收发存明细列表
     */
    @GetMapping("/stock-transactions")
    public Result<List<StockTransactionDTO>> getStockTransactions(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) String transactionType) {
        try {
            List<StockTransactionDTO> result = reportService.getStockTransactions(
                    startDate, endDate, warehouseId, materialId, transactionType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询收发存明细失败: " + e.getMessage());
        }
    }

    // ==================== 库龄分析 ====================

    /**
     * 获取库龄分析报表
     * @param warehouseId 仓库ID（可选）
     * @param categoryId 分类ID（可选）
     * @param minDays 最小库龄天数（可选）
     * @return 库龄分析列表
     */
    @GetMapping("/inventory-age")
    public Result<List<InventoryAgeDTO>> getInventoryAge(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minDays) {
        try {
            List<InventoryAgeDTO> result = reportService.getInventoryAge(warehouseId, categoryId, minDays);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询库龄分析失败: " + e.getMessage());
        }
    }

    // ==================== 上架效率报表 ====================

    /**
     * 获取上架效率报表
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param operatorId 操作员ID（可选）
     * @return 上架效率列表
     */
    @GetMapping("/instock-efficiency")
    public Result<List<EfficiencyDTO>> getInStockEfficiency(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long operatorId) {
        try {
            List<EfficiencyDTO> result = reportService.getInStockEfficiency(startDate, endDate, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询上架效率失败: " + e.getMessage());
        }
    }

    // ==================== 拣货效率报表 ====================

    /**
     * 获取拣货效率报表
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param operatorId 操作员ID（可选）
     * @return 拣货效率列表
     */
    @GetMapping("/outstock-efficiency")
    public Result<List<EfficiencyDTO>> getOutStockEfficiency(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long operatorId) {
        try {
            List<EfficiencyDTO> result = reportService.getOutStockEfficiency(startDate, endDate, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询拣货效率失败: " + e.getMessage());
        }
    }

    // ==================== 盘点差异报表 ====================

    /**
     * 获取盘点差异报表
     * @param startDate 开始日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param endDate 结束日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param warehouseId 仓库ID（可选）
     * @return 盘点差异列表
     */
    @GetMapping("/check-diff")
    public Result<List<CheckDiffDTO>> getCheckDifferences(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(required = false) Long warehouseId) {
        try {
            List<CheckDiffDTO> result = reportService.getCheckDifferences(startDate, endDate, warehouseId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询盘点差异失败: " + e.getMessage());
        }
    }

    // ==================== 可视化看板 - 库存分布 ====================

    /**
     * 获取库存分布数据（按仓库）
     * @return 库存分布列表
     */
    @GetMapping("/inventory-distribution/warehouse")
    public Result<List<InventoryDistributionDTO>> getInventoryDistributionByWarehouse() {
        try {
            List<InventoryDistributionDTO> result = reportService.getInventoryDistributionByWarehouse();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询库存分布（按仓库）失败: " + e.getMessage());
        }
    }

    /**
     * 获取库存分布数据（按分类）
     * @return 库存分布列表
     */
    @GetMapping("/inventory-distribution/category")
    public Result<List<InventoryDistributionDTO>> getInventoryDistributionByCategory() {
        try {
            List<InventoryDistributionDTO> result = reportService.getInventoryDistributionByCategory();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询库存分布（按分类）失败: " + e.getMessage());
        }
    }

    // ==================== 可视化看板 - 出入库趋势 ====================

    /**
     * 获取出入库趋势数据
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param warehouseId 仓库ID（可选）
     * @return 出入库趋势列表
     */
    @GetMapping("/inout-trend")
    public Result<List<InOutTrendDTO>> getInOutTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long warehouseId) {
        try {
            List<InOutTrendDTO> result = reportService.getInOutTrend(startDate, endDate, warehouseId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询出入库趋势失败: " + e.getMessage());
        }
    }
}
