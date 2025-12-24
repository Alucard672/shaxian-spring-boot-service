package com.shaxian.biz.dto.dyeing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "更新染色订单请求")
public class UpdateDyeingOrderRequest {
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", example = "纯棉纱线")
    private String productName;

    @Schema(description = "坯布缸号ID", example = "1")
    private Long greyBatchId;

    @Size(max = 50, message = "坯布缸号编码长度不能超过50")
    @Schema(description = "坯布缸号编码", example = "GREY001")
    private String greyBatchCode;

    @Schema(description = "工厂ID", example = "1")
    private Long factoryId;

    @Size(max = 200, message = "工厂名称长度不能超过200")
    @Schema(description = "工厂名称", example = "染色工厂A")
    private String factoryName;

    @Size(max = 50, message = "工厂电话长度不能超过50")
    @Schema(description = "工厂电话", example = "13800138000")
    private String factoryPhone;

    @Schema(description = "发货日期", example = "2024-01-01")
    private LocalDate shipmentDate;

    @Schema(description = "预期完成日期", example = "2024-01-15")
    private LocalDate expectedCompletionDate;

    @Schema(description = "实际完成日期", example = "2024-01-14")
    private LocalDate actualCompletionDate;

    @Schema(description = "加工单价", example = "10.00")
    private BigDecimal processingPrice;

    @Schema(description = "订单状态", example = "PROCESSING", allowableValues = {"PENDING_SHIPMENT", "PROCESSING", "COMPLETED", "RECEIVED", "CANCELLED"})
    private String status; // PENDING_SHIPMENT, PROCESSING, COMPLETED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", example = "张三")
    private String operator;

    @Schema(description = "备注", example = "加急订单")
    private String remark;

    @Valid
    @Schema(description = "订单明细列表")
    private List<DyeingOrderItemRequest> items;
}
