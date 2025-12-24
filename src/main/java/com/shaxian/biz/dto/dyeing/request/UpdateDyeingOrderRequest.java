package com.shaxian.biz.dto.dyeing.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateDyeingOrderRequest {
    private Long productId;

    @Size(max = 200, message = "商品名称长度不能超过200")
    private String productName;

    private Long greyBatchId;

    @Size(max = 50, message = "坯布缸号编码长度不能超过50")
    private String greyBatchCode;

    private Long factoryId;

    @Size(max = 200, message = "工厂名称长度不能超过200")
    private String factoryName;

    @Size(max = 50, message = "工厂电话长度不能超过50")
    private String factoryPhone;

    private LocalDate shipmentDate;

    private LocalDate expectedCompletionDate;

    private LocalDate actualCompletionDate;

    private BigDecimal processingPrice;

    private String status; // PENDING_SHIPMENT, PROCESSING, COMPLETED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    private String operator;

    private String remark;

    @Valid
    private List<DyeingOrderItemRequest> items;
}
