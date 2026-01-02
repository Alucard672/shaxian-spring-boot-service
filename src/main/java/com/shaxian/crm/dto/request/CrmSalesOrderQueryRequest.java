package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "软件销售订单查询请求")
public class CrmSalesOrderQueryRequest {
    @Schema(description = "CRM客户ID", example = "1")
    private Long crmCustomerId;

    @Schema(description = "订单状态", example = "DRAFT", allowableValues = {"DRAFT", "PAID", "REVIEWED", "CANCELLED"})
    private String status;

    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-12-31")
    private LocalDate endDate;
}

