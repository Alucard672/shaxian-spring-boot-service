package com.shaxian.biz.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建付款记录请求")
public class CreatePaymentRecordRequest {
    @NotNull(message = "付款金额不能为空")
    @Schema(description = "付款金额", required = true, example = "5000.00")
    private BigDecimal amount;

    @NotNull(message = "付款日期不能为空")
    @Schema(description = "付款日期", required = true, example = "2024-01-01")
    private LocalDate paymentDate;

    @Size(max = 50, message = "付款方式长度不能超过50")
    @Schema(description = "付款方式", example = "银行转账")
    private String paymentMethod;

    @Size(max = 200, message = "备注长度不能超过200")
    @Schema(description = "备注", example = "部分付款")
    private String remark;
}
