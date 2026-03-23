package com.andy.warehouse.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryDTO {

    private BigDecimal totalInventoryAmount;

    private BigDecimal totalInventoryQuantity;

    private Long totalMaterialCount;

    private Long totalWarehouseCount;

    private BigDecimal todayStockInQuantity;

    private BigDecimal todayStockOutQuantity;

    private BigDecimal monthStockInAmount;

    private BigDecimal monthStockOutAmount;

    private Long lowStockCount;

    private Long expiredCount;

    private List<WarehouseInventoryDTO> warehouseDistribution;

    private List<TrendDataDTO> stockInTrend;

    private List<TrendDataDTO> stockOutTrend;

    private List<CategoryDistributionDTO> categoryDistribution;

    @Data
    @Builder
    public static class WarehouseInventoryDTO {
        private Long warehouseId;
        private String warehouseName;
        private BigDecimal quantity;
        private BigDecimal amount;
        private Double percentage;
    }

    @Data
    @Builder
    public static class TrendDataDTO {
        private String date;
        private BigDecimal quantity;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class CategoryDistributionDTO {
        private String categoryName;
        private BigDecimal amount;
        private Double percentage;
    }
}
