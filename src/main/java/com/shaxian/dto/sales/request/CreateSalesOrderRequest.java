package com.shaxian.dto.sales.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateSalesOrderRequest {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200")
    private String customerName;

    @NotNull(message = "销售日期不能为空")
    private LocalDate salesDate;

    private LocalDate expectedDate;

    private String status; // DRAFT, PENDING, APPROVED, SHIPPED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<SalesOrderItemRequest> items;
}
