package com.andy.warehouse.dto.report;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReportQueryRequest {

    private Long warehouseId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private Long categoryId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String periodType;

    private String groupBy;

    private Integer pageNum = 1;

    private Integer pageSize = 20;
}
