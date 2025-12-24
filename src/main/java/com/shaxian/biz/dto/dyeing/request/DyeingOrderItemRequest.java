package com.shaxian.biz.dto.dyeing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "染色订单明细请求")
public class DyeingOrderItemRequest {
    @NotNull(message = "目标色号ID不能为空")
    @Schema(description = "目标色号ID", required = true, example = "1")
    private Long targetColorId;

    @Size(max = 50, message = "目标色号编码长度不能超过50")
    @Schema(description = "目标色号编码", example = "COLOR001")
    private String targetColorCode;

    @Size(max = 100, message = "目标色号名称长度不能超过100")
    @Schema(description = "目标色号名称", example = "红色")
    private String targetColorName;

    @Size(max = 20, message = "目标颜色值长度不能超过20")
    @Schema(description = "目标颜色值", example = "#FF0000")
    private String targetColorValue;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", required = true, example = "1000.00")
    private BigDecimal quantity;
}
