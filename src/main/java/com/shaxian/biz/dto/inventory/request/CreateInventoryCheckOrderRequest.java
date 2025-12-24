package com.shaxian.biz.dto.inventory.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateInventoryCheckOrderRequest {
    @NotBlank(message = "盘点单名称不能为空")
    @Size(max = 200, message = "盘点单名称长度不能超过200")
    private String name;

    @NotBlank(message = "仓库不能为空")
    @Size(max = 100, message = "仓库长度不能超过100")
    private String warehouse;

    @NotNull(message = "计划日期不能为空")
    private LocalDate planDate;

    private String status; // PLANNED, CHECKING, COMPLETED, CANCELLED

    @NotNull(message = "操作员不能为空")
    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<InventoryCheckItemRequest> items;
}
