package com.shaxian.biz.dto.dyeing.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DyeingOrderItemRequest {
    @NotNull(message = "目标色号ID不能为空")
    private Long targetColorId;

    @Size(max = 50, message = "目标色号编码长度不能超过50")
    private String targetColorCode;

    @Size(max = 100, message = "目标色号名称长度不能超过100")
    private String targetColorName;

    @Size(max = 20, message = "目标颜色值长度不能超过20")
    private String targetColorValue;

    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;
}
