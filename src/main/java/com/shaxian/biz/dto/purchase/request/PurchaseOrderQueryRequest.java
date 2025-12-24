package com.shaxian.biz.dto.purchase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "采购订单查询请求")
public class PurchaseOrderQueryRequest {
    @Schema(description = "订单状态", example = "APPROVED", allowableValues = {"DRAFT", "PENDING", "APPROVED", "RECEIVED", "CANCELLED"})
    private String status;
    
    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;
    
    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;
    
    @Schema(description = "结束日期", example = "2024-12-31")
    private LocalDate endDate;
}
