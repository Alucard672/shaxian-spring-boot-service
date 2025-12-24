package com.shaxian.biz.dto.purchase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdatePurchaseOrderRequest {
    @Size(max = 200, message = "供应商名称长度不能超过200")
    private String supplierName;

    private LocalDate purchaseDate;

    private LocalDate expectedDate;

    private String status; // DRAFT, PENDING, APPROVED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @Valid
    private List<PurchaseOrderItemRequest> items;
}
