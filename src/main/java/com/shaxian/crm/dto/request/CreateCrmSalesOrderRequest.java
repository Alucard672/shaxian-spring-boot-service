package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "创建软件销售订单请求")
public class CreateCrmSalesOrderRequest {
    @NotNull(message = "CRM客户ID不能为空")
    @Schema(description = "CRM客户ID", required = true, example = "1")
    private Long crmCustomerId;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", required = true, example = "张三公司")
    private String customerName;

    @NotNull(message = "销售日期不能为空")
    @Schema(description = "销售日期", required = true, example = "2024-01-01")
    private LocalDate salesDate;

    @Schema(description = "已付金额", example = "0.00")
    private BigDecimal paidAmount;

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

