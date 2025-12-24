package com.shaxian.biz.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新商品请求")
public class UpdateProductRequest {
    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", example = "纯棉纱线")
    private String name;

    @Size(max = 50, message = "商品编码长度不能超过50")
    @Schema(description = "商品编码", example = "PROD001")
    private String code;

    @Size(max = 100, message = "规格长度不能超过100")
    @Schema(description = "规格", example = "40支")
    private String specification;

    @Size(max = 200, message = "成分长度不能超过200")
    @Schema(description = "成分", example = "100%纯棉")
    private String composition;

    @Size(max = 50, message = "支数长度不能超过50")
    @Schema(description = "支数", example = "40")
    private String count;

    @Schema(description = "单位", example = "kg")
    private String unit;

    @Schema(description = "商品类型", example = "RAW_MATERIAL", allowableValues = {"RAW_MATERIAL", "SEMI_FINISHED", "FINISHED"})
    private String type; // RAW_MATERIAL, SEMI_FINISHED, FINISHED

    @Schema(description = "是否为白纱", example = "false")
    private Boolean isWhiteYarn;

    @Schema(description = "描述", example = "优质纯棉纱线")
    private String description;
}
