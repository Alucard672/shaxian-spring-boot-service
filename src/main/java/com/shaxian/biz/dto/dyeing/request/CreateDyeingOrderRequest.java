package com.shaxian.biz.dto.dyeing.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "创建染色订单请求")
public class CreateDyeingOrderRequest {
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true, example = "1")
    private Long productId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", required = true, example = "纯棉纱线")
    private String productName;

    @NotNull(message = "坯布缸号ID不能为空")
    @Schema(description = "坯布缸号ID", required = true, example = "1")
    private Long greyBatchId;

    @NotBlank(message = "坯布缸号编码不能为空")
    @Size(max = 50, message = "坯布缸号编码长度不能超过50")
    @Schema(description = "坯布缸号编码", required = true, example = "GREY001")
    private String greyBatchCode;

    @Schema(description = "工厂ID", example = "1")
    private Long factoryId;

    @NotBlank(message = "工厂名称不能为空")
    @Size(max = 200, message = "工厂名称长度不能超过200")
    @Schema(description = "工厂名称", required = true, example = "染色工厂A")
    private String factoryName;

    @Size(max = 50, message = "工厂电话长度不能超过50")
    @Schema(description = "工厂电话", example = "13800138000")
    private String factoryPhone;

    @NotNull(message = "发货日期不能为空")
    @Schema(description = "发货日期", required = true, example = "2024-01-01")
    private LocalDate shipmentDate;

    @NotNull(message = "预期完成日期不能为空")
    @Schema(description = "预期完成日期", required = true, example = "2024-01-15")
    private LocalDate expectedCompletionDate;

    @NotNull(message = "加工单价不能为空")
    @Schema(description = "加工单价", required = true, example = "10.00")
    private BigDecimal processingPrice;

    @Schema(description = "订单状态", example = "PENDING_SHIPMENT", allowableValues = {"PENDING_SHIPMENT", "PROCESSING", "COMPLETED", "RECEIVED", "CANCELLED"})
    private String status; // PENDING_SHIPMENT, PROCESSING, COMPLETED, RECEIVED, CANCELLED

    @Size(max = 100, message = "操作员长度不能超过100")
    @Schema(description = "操作员", example = "张三")
    private String operator;

    @Schema(description = "备注", example = "加急订单")
    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    @Schema(description = "订单明细列表", required = true)
    private List<DyeingOrderItemRequest> items;
}
