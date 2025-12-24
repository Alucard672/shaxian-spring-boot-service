package com.shaxian.biz.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建应收账款请求")
public class CreateAccountReceivableRequest {
    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", required = true, example = "1")
    private Long customerId;

    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", example = "张三公司")
    private String customerName;

    @NotNull(message = "应收金额不能为空")
    @Schema(description = "应收金额", required = true, example = "10000.00")
    private BigDecimal amount;

    @NotNull(message = "应收日期不能为空")
    @Schema(description = "应收日期", required = true, example = "2024-01-01")
    private LocalDate receivableDate;

    @Schema(description = "到期日期", example = "2024-01-31")
    private LocalDate dueDate;

    @Schema(description = "收款状态", example = "UNPAID", allowableValues = {"UNPAID", "PARTIALLY_PAID", "PAID"})
    private String status; // UNPAID, PARTIALLY_PAID, PAID

    @Size(max = 50, message = "订单号长度不能超过50")
    @Schema(description = "订单号", example = "SO001")
    private String orderNumber;

    @Schema(description = "备注", example = "销售订单收款")
    private String remark;
}
