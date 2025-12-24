package com.shaxian.biz.dto.inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "创建库存调整单请求")
public class CreateAdjustmentOrderRequest {
    @NotNull(message = "调整类型不能为空")
    @Schema(description = "调整类型", required = true, example = "INCREASE", allowableValues = {"INCREASE", "DECREASE", "SURPLUS", "DEFICIT", "LOSS", "OTHER"})
    private String type; // INCREASE, DECREASE, SURPLUS, DEFICIT, LOSS, OTHER

    @NotNull(message = "调整日期不能为空")
    @Schema(description = "调整日期", required = true, example = "2024-01-01")
    private LocalDate adjustmentDate;

    @Schema(description = "调整单状态", example = "DRAFT", allowableValues = {"DRAFT", "COMPLETED"})
    private String status; // DRAFT, COMPLETED

    @NotNull(message = "操作员不能为空")
    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", required = true, example = "张三")
    private String operator;

    @Schema(description = "备注", example = "库存调整")
    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    @Schema(description = "调整明细列表", required = true)
    private List<AdjustmentOrderItemRequest> items;
}
