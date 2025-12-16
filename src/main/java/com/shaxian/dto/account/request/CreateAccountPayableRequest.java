package com.shaxian.dto.account.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateAccountPayableRequest {
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @Size(max = 200, message = "供应商名称长度不能超过200")
    private String supplierName;

    @NotNull(message = "应付金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "应付日期不能为空")
    private LocalDate payableDate;

    private LocalDate dueDate;

    private String status; // UNPAID, PARTIALLY_PAID, PAID

    @Size(max = 50, message = "订单号长度不能超过50")
    private String orderNumber;

    private String remark;
}
