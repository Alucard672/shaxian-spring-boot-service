package com.shaxian.biz.dto.inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "更新库存盘点单请求")
public class UpdateInventoryCheckOrderRequest {
    @Size(max = 200, message = "盘点单名称长度不能超过200")
    @Schema(description = "盘点单名称", example = "2024年1月盘点")
    private String name;

    @Size(max = 100, message = "仓库长度不能超过100")
    @Schema(description = "仓库", example = "A区-1号仓库")
    private String warehouse;

    @Schema(description = "计划日期", example = "2024-01-01")
    private LocalDate planDate;

    @Schema(description = "盘点状态", example = "PLANNED", allowableValues = {"PLANNED", "CHECKING", "COMPLETED", "CANCELLED"})
    private String status; // PLANNED, CHECKING, COMPLETED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", example = "张三")
    private String operator;

    @Schema(description = "备注", example = "月度盘点")
    private String remark;

    @Valid
    @Schema(description = "盘点明细列表")
    private List<InventoryCheckItemRequest> items;
}
