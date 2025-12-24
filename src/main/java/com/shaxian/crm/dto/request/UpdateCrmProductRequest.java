package com.shaxian.crm.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCrmProductRequest {
    @Size(max = 200, message = "商品名称长度不能超过200")
    private String name;

    @Size(max = 50, message = "商品编码长度不能超过50")
    private String code;

    private BigDecimal unitPrice;

    private BigDecimal discountPrice;

    private BigDecimal productValue;

    private Integer licenseCount;

    private String status; // ACTIVE, INACTIVE

    private String description;
}

