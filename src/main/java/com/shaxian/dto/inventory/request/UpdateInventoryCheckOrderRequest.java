package com.shaxian.dto.inventory.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateInventoryCheckOrderRequest {
    @Size(max = 200, message = "盘点单名称长度不能超过200")
    private String name;

    @Size(max = 100, message = "仓库长度不能超过100")
    private String warehouse;

    private LocalDate planDate;

    private String status; // PLANNED, CHECKING, COMPLETED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @Valid
    private List<InventoryCheckItemRequest> items;
}
