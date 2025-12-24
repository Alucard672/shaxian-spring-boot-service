package com.shaxian.biz.dto.purchase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "采购订单明细请求")
public class PurchaseOrderItemRequest {
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true, example = "1")
    private Long productId;

    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", example = "纯棉纱线")
    private String productName;

    @Size(max = 50, message = "商品编码长度不能超过50")
    @Schema(description = "商品编码", example = "PROD001")
    private String productCode;

    @Schema(description = "色号ID", example = "1")
    private Long colorId;

    @Size(max = 100, message = "色号名称长度不能超过100")
    @Schema(description = "色号名称", example = "红色")
    private String colorName;

    @Size(max = 50, message = "色号编码长度不能超过50")
    @Schema(description = "色号编码", example = "COLOR001")
    private String colorCode;

    @NotBlank(message = "缸号编码不能为空")
    @Size(max = 50, message = "缸号编码长度不能超过50")
    @Schema(description = "缸号编码", required = true, example = "BATCH001")
    private String batchCode;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", required = true, example = "1000.00")
    private BigDecimal quantity;

    @Size(max = 20, message = "单位长度不能超过20")
    @Schema(description = "单位", example = "kg")
    private String unit;

    @NotNull(message = "单价不能为空")
    @Schema(description = "单价", required = true, example = "50.00")
    private BigDecimal price;

    @Schema(description = "生产日期", example = "2024-01-01")
    private LocalDate productionDate;

    @Size(max = 100, message = "库存位置长度不能超过100")
    @Schema(description = "库存位置", example = "A区-1号仓库")
    private String stockLocation;

    @Schema(description = "备注", example = "优质产品")
    private String remark;
}
