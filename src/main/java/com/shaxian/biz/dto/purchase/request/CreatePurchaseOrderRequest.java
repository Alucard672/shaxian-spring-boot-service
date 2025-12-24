package com.shaxian.biz.dto.purchase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePurchaseOrderRequest {
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 200, message = "供应商名称长度不能超过200")
    private String supplierName;

    @NotNull(message = "进货日期不能为空")
    private LocalDate purchaseDate;

    private LocalDate expectedDate;

    private String status; // DRAFT, PENDING, APPROVED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}
