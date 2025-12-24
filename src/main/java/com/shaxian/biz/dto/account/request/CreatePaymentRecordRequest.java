package com.shaxian.biz.dto.account.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreatePaymentRecordRequest {
    @NotNull(message = "付款金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "付款日期不能为空")
    private LocalDate paymentDate;

    @Size(max = 50, message = "付款方式长度不能超过50")
    private String paymentMethod;

    @Size(max = 200, message = "备注长度不能超过200")
    private String remark;
}
