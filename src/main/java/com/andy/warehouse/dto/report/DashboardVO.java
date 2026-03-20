package com.andy.warehouse.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {

    private StockOverview stockOverview;
    private List<StockDistributionVO> stockDistribution;
    private List<StockTrendVO> stockTrend;
    private List<MaterialStockAlert> stockAlerts;
    private TodayStatistics todayStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockOverview {
        private Integer totalMaterials;
        private Integer totalQuantity;
        private BigDecimal totalAmount;
        private Integer warehouseCount;
        private Integer alertCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayStatistics {
        private Integer todayInOrders;
        private Integer todayOutOrders;
        private Integer todayInQuantity;
        private Integer todayOutQuantity;
        private BigDecimal todayInAmount;
        private BigDecimal todayOutAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialStockAlert {
        private Long materialId;
        private String materialCode;
        private String materialName;
        private Integer currentQuantity;
        private Integer safetyStock;
        private String alertType;
    }
}
