package com.warehouse.management.service;

import com.warehouse.management.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表服务接口
 * 定义库存报表、效率报表、可视化看板等相关查询方法
 */
public interface ReportService {

    // ==================== 库存汇总报表 ====================

    /**
     * 查询库存汇总报表
     * @param warehouseId 仓库ID（可选）
     * @param categoryId 分类ID（可选）
     * @param materialId 物料ID（可选）
     * @return 库存汇总列表
     */
    List<InventorySummaryDTO> getInventorySummary(Long warehouseId, Long categoryId, Long materialId);

    // ==================== 收发存明细表 ====================

    /**
     * 查询收发存明细
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param warehouseId 仓库ID（可选）
     * @param materialId 物料ID（可选）
     * @param transactionType 交易类型（可选：IN/OUT）
     * @return 收发存明细列表
     */
    List<StockTransactionDTO> getStockTransactions(LocalDateTime startDate, LocalDateTime endDate,
                                                    Long warehouseId, Long materialId, String transactionType);

    // ==================== 库龄分析 ====================

    /**
     * 查询库龄分析数据
     * @param warehouseId 仓库ID（可选）
     * @param categoryId 分类ID（可选）
     * @param minDays 最小库龄天数（可选）
     * @return 库龄分析列表
     */
    List<InventoryAgeDTO> getInventoryAge(Long warehouseId, Long categoryId, Integer minDays);

    // ==================== 拣货/上架效率报表 ====================

    /**
     * 查询上架效率统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param operatorId 操作员ID（可选）
     * @return 上架效率列表
     */
    List<EfficiencyDTO> getInStockEfficiency(LocalDate startDate, LocalDate endDate, Long operatorId);

    /**
     * 查询拣货效率统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param operatorId 操作员ID（可选）
     * @return 拣货效率列表
     */
    List<EfficiencyDTO> getOutStockEfficiency(LocalDate startDate, LocalDate endDate, Long operatorId);

    // ==================== 盘点差异报表 ====================

    /**
     * 查询盘点差异报表（模拟数据，实际需要盘点单支持）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param warehouseId 仓库ID（可选）
     * @return 盘点差异列表
     */
    List<CheckDiffDTO> getCheckDifferences(LocalDateTime startDate, LocalDateTime endDate, Long warehouseId);

    // ==================== 可视化看板 ====================

    /**
     * 查询库存分布数据（按仓库）
     * @return 库存分布列表
     */
    List<InventoryDistributionDTO> getInventoryDistributionByWarehouse();

    /**
     * 查询库存分布数据（按分类）
     * @return 库存分布列表
     */
    List<InventoryDistributionDTO> getInventoryDistributionByCategory();

    /**
     * 查询出入库趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param warehouseId 仓库ID（可选）
     * @return 出入库趋势列表
     */
    List<InOutTrendDTO> getInOutTrend(LocalDate startDate, LocalDate endDate, Long warehouseId);
}
