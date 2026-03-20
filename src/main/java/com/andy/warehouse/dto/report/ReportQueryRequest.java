package com.andy.warehouse.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportQueryRequest {

    private Long warehouseId;
    private Long materialId;
    private Long categoryId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
