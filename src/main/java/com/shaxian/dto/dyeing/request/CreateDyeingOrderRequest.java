package com.shaxian.dto.dyeing.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDyeingOrderRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200")
    private String productName;

    @NotNull(message = "坯布缸号ID不能为空")
    private Long greyBatchId;

    @NotBlank(message = "坯布缸号编码不能为空")
    @Size(max = 50, message = "坯布缸号编码长度不能超过50")
    private String greyBatchCode;

    private Long factoryId;

    @NotBlank(message = "工厂名称不能为空")
    @Size(max = 200, message = "工厂名称长度不能超过200")
    private String factoryName;

    @Size(max = 50, message = "工厂电话长度不能超过50")
    private String factoryPhone;

    @NotNull(message = "发货日期不能为空")
    private LocalDate shipmentDate;

    @NotNull(message = "预期完成日期不能为空")
    private LocalDate expectedCompletionDate;

    @NotNull(message = "加工单价不能为空")
    private BigDecimal processingPrice;

    private String status; // PENDING_SHIPMENT, PROCESSING, COMPLETED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<DyeingOrderItemRequest> items;
}
