package com.shaxian.biz.dto.sales.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "销售订单查询请求")
public class SalesOrderQueryRequest {
    @Schema(description = "订单状态", example = "APPROVED", allowableValues = {"DRAFT", "PENDING", "APPROVED", "SHIPPED", "CANCELLED"})
    private String status;
    
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    
    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;
    
    @Schema(description = "结束日期", example = "2024-12-31")
    private LocalDate endDate;
}
