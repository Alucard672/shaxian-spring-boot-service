package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CRM商品查询请求")
public class CrmProductQueryRequest {
    @Schema(description = "商品名称（模糊查询）", example = "CRM产品")
    private String name;

    @Schema(description = "商品编码（模糊查询）", example = "CRM001")
    private String code;

    @Schema(description = "商品状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}

