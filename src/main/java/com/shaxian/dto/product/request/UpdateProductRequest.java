package com.shaxian.dto.product.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductRequest {
    @Size(max = 200, message = "商品名称长度不能超过200")
    private String name;

    @Size(max = 50, message = "商品编码长度不能超过50")
    private String code;

    @Size(max = 100, message = "规格长度不能超过100")
    private String specification;

    @Size(max = 200, message = "成分长度不能超过200")
    private String composition;

    @Size(max = 50, message = "支数长度不能超过50")
    private String count;

    private String unit;

    private String type; // RAW_MATERIAL, SEMI_FINISHED, FINISHED

    private Boolean isWhiteYarn;

    private String description;
}
