package com.shaxian.dto.product.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateColorRequest {
    @Size(max = 50, message = "色号编码长度不能超过50")
    private String code;

    @Size(max = 100, message = "色号名称长度不能超过100")
    private String name;

    @Size(max = 20, message = "颜色值长度不能超过20")
    private String colorValue;

    private String description;

    private String status; // ON_SALE, DISCONTINUED
}
