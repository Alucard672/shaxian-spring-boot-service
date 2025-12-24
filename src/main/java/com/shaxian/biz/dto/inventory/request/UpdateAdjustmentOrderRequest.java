package com.shaxian.biz.dto.inventory.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateAdjustmentOrderRequest {
    private String type; // INCREASE, DECREASE, SURPLUS, DEFICIT, LOSS, OTHER

    private LocalDate adjustmentDate;

    private String status; // DRAFT, COMPLETED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @Valid
    private List<AdjustmentOrderItemRequest> items;
}
