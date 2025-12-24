package com.shaxian.biz.dto.inventory.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAdjustmentOrderRequest {
    @NotNull(message = "调整类型不能为空")
    private String type; // INCREASE, DECREASE, SURPLUS, DEFICIT, LOSS, OTHER

    @NotNull(message = "调整日期不能为空")
    private LocalDate adjustmentDate;

    private String status; // DRAFT, COMPLETED

    @NotNull(message = "操作员不能为空")
    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<AdjustmentOrderItemRequest> items;
}
