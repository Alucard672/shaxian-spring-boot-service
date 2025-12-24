package com.shaxian.biz.dto.purchase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "创建采购订单请求")
public class CreatePurchaseOrderRequest {
    @NotNull(message = "供应商ID不能为空")
    @Schema(description = "供应商ID", required = true, example = "1")
    private Long supplierId;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 200, message = "供应商名称长度不能超过200")
    @Schema(description = "供应商名称", required = true, example = "供应商A")
    private String supplierName;

    @NotNull(message = "进货日期不能为空")
    @Schema(description = "进货日期", required = true, example = "2024-01-01")
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

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    @Schema(description = "订单明细列表", required = true)
    private List<PurchaseOrderItemRequest> items;
}
