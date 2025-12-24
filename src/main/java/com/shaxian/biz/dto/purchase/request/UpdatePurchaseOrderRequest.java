package com.shaxian.biz.dto.purchase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "更新采购订单请求")
public class UpdatePurchaseOrderRequest {
    @Size(max = 200, message = "供应商名称长度不能超过200")
    @Schema(description = "供应商名称", example = "供应商A")
    private String supplierName;

    @Schema(description = "进货日期", example = "2024-01-01")
    private LocalDate purchaseDate;

    @Schema(description = "预计到货日期", example = "2024-01-15")
    private LocalDate expectedDate;

    @Schema(description = "订单状态", example = "DRAFT", allowableValues = {"DRAFT", "PENDING", "APPROVED", "RECEIVED", "CANCELLED"})
    private String status; // DRAFT, PENDING, APPROVED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", example = "张三")
    private String operator;

    @Schema(description = "备注", example = "加急采购")
    private String remark;

    @Valid
    @Schema(description = "订单明细列表")
    private List<PurchaseOrderItemRequest> items;
}
