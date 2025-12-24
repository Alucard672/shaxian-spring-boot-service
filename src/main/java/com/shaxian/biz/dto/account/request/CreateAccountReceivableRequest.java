package com.shaxian.biz.dto.account.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateAccountReceivableRequest {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Size(max = 200, message = "客户名称长度不能超过200")
    private String customerName;

    @NotNull(message = "应收金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "应收日期不能为空")
    private LocalDate receivableDate;

    private LocalDate dueDate;

    private String status; // UNPAID, PARTIALLY_PAID, PAID

    @Size(max = 50, message = "订单号长度不能超过50")
    private String orderNumber;

    private String remark;
}
