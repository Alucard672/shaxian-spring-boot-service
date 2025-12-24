package com.shaxian.biz.dto.sales.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "销售订单明细请求")
public class SalesOrderItemRequest {
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true, example = "1")
    private Long productId;

    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", example = "纯棉纱线")
    private String productName;

    @Size(max = 50, message = "商品编码长度不能超过50")
    @Schema(description = "商品编码", example = "PROD001")
    private String productCode;

    @NotNull(message = "色号ID不能为空")
    @Schema(description = "色号ID", required = true, example = "1")
    private Long colorId;

    @Size(max = 100, message = "色号名称长度不能超过100")
    @Schema(description = "色号名称", example = "红色")
    private String colorName;

    @Size(max = 50, message = "色号编码长度不能超过50")
    @Schema(description = "色号编码", example = "COLOR001")
    private String colorCode;

    @NotNull(message = "缸号ID不能为空")
    @Schema(description = "缸号ID", required = true, example = "1")
    private Long batchId;

    @Size(max = 50, message = "缸号编码长度不能超过50")
    @Schema(description = "缸号编码", example = "BATCH001")
    private String batchCode;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", required = true, example = "100.00")
    private BigDecimal quantity;

    @Size(max = 20, message = "单位长度不能超过20")
    @Schema(description = "单位", example = "kg")
    private String unit;

    @NotNull(message = "单价不能为空")
    @Schema(description = "单价", required = true, example = "50.00")
    private BigDecimal price;

    @Schema(description = "备注", example = "优质产品")
    private String remark;
}
