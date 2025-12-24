package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新CRM商品请求")
public class UpdateCrmProductRequest {
    @Size(max = 200, message = "商品名称长度不能超过200")
    @Schema(description = "商品名称", example = "CRM产品A")
    private String name;

    @Size(max = 50, message = "商品编码长度不能超过50")
    @Schema(description = "商品编码", example = "CRM001")
    private String code;

    @Schema(description = "单价", example = "1000.00")
    private BigDecimal unitPrice;

    @Schema(description = "折扣价", example = "800.00")
    private BigDecimal discountPrice;

    @Schema(description = "产品价值", example = "1200.00")
    private BigDecimal productValue;

    @Schema(description = "许可证数量", example = "10")
    private Integer licenseCount;

    @Schema(description = "商品状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status; // ACTIVE, INACTIVE

    @Schema(description = "描述", example = "CRM产品描述")
    private String description;
}

