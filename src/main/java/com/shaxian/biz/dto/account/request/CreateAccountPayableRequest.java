package com.shaxian.biz.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建应付账款请求")
public class CreateAccountPayableRequest {
    @NotNull(message = "供应商ID不能为空")
    @Schema(description = "供应商ID", required = true, example = "1")
    private Long supplierId;

    @Size(max = 200, message = "供应商名称长度不能超过200")
    @Schema(description = "供应商名称", example = "供应商A")
    private String supplierName;

    @NotNull(message = "应付金额不能为空")
    @Schema(description = "应付金额", required = true, example = "10000.00")
    private BigDecimal amount;

    @NotNull(message = "应付日期不能为空")
    @Schema(description = "应付日期", required = true, example = "2024-01-01")
    private LocalDate payableDate;

    @Schema(description = "到期日期", example = "2024-01-31")
    private LocalDate dueDate;

    @Schema(description = "付款状态", example = "UNPAID", allowableValues = {"UNPAID", "PARTIALLY_PAID", "PAID"})
    private String status; // UNPAID, PARTIALLY_PAID, PAID

    @Size(max = 50, message = "订单号长度不能超过50")
    @Schema(description = "订单号", example = "PO001")
    private String orderNumber;

    @Schema(description = "备注", example = "采购订单付款")
    private String remark;
}
