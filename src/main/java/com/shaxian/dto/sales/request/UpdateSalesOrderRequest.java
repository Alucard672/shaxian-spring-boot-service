package com.shaxian.dto.sales.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateSalesOrderRequest {
    @Size(max = 200, message = "客户名称长度不能超过200")
    private String customerName;

    private LocalDate salesDate;

    private LocalDate expectedDate;

    private String status; // DRAFT, PENDING, APPROVED, SHIPPED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @Valid
    private List<SalesOrderItemRequest> items;
}
