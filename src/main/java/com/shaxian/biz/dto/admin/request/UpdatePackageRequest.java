package com.shaxian.biz.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新套餐请求")
public class UpdatePackageRequest {
    @Size(max = 100, message = "套餐名长度不能超过 100")
    @Schema(description = "套餐名")
    private String name;

    @Min(value = 1, message = "并发上限必须 ≥ 1")
    @Schema(description = "并发上限")
    private Integer concurrentLimit;

    @PositiveOrZero(message = "年单价必须 ≥ 0")
    @Schema(description = "年单价（元）")
    private BigDecimal yearlyPrice;

    @Schema(description = "状态", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
