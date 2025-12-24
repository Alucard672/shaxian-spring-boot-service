package com.shaxian.biz.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建色号请求")
public class CreateColorRequest {
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true, example = "1")
    private Long productId;

    @NotBlank(message = "色号编码不能为空")
    @Size(max = 50, message = "色号编码长度不能超过50")
    @Schema(description = "色号编码", required = true, example = "COLOR001")
    private String code;

    @NotBlank(message = "色号名称不能为空")
    @Size(max = 100, message = "色号名称长度不能超过100")
    @Schema(description = "色号名称", required = true, example = "红色")
    private String name;

    @Size(max = 20, message = "颜色值长度不能超过20")
    @Schema(description = "颜色值", example = "#FF0000")
    private String colorValue;

    @Schema(description = "描述", example = "标准红色")
    private String description;

    @Schema(description = "色号状态", example = "ON_SALE", allowableValues = {"ON_SALE", "DISCONTINUED"})
    private String status; // ON_SALE, DISCONTINUED
}
