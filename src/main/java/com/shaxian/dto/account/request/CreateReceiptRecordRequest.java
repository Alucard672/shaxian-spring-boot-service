package com.shaxian.dto.account.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateReceiptRecordRequest {
    @NotNull(message = "收款金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "收款日期不能为空")
    private LocalDate receiptDate;

    @Size(max = 50, message = "收款方式长度不能超过50")
    private String paymentMethod;

    @Size(max = 200, message = "备注长度不能超过200")
    private String remark;
}
