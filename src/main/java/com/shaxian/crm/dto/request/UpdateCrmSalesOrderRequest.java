package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "更新软件销售订单请求")
public class UpdateCrmSalesOrderRequest {
    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", example = "张三公司")
    private String customerName;

    @Schema(description = "销售日期", example = "2024-01-01")
    private LocalDate salesDate;

    @Schema(description = "已付金额", example = "1000.00")
    private BigDecimal paidAmount;

    @Schema(description = "订单状态", example = "DRAFT", allowableValues = {"DRAFT", "PAID", "REVIEWED", "CANCELLED"})
    private String status;

    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", example = "张三")
    private String operator;

    @Schema(description = "备注", example = "软件销售订单")
    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    @Schema(description = "订单明细列表", required = true)
    private List<CrmSalesOrderItemRequest> items;
}

