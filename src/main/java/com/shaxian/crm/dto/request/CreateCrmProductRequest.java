package com.shaxian.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCrmProductRequest {
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200")
    private String name;

    @NotBlank(message = "商品编码不能为空")
    @Size(max = 50, message = "商品编码长度不能超过50")
    private String code;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    private BigDecimal discountPrice;

    private BigDecimal productValue;

    private Integer licenseCount;

    private String status; // ACTIVE, INACTIVE

    private String description;
}

