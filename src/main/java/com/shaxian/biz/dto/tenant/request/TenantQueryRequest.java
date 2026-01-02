package com.shaxian.biz.dto.tenant.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户查询请求")
public class TenantQueryRequest {
    @Schema(description = "租户名称（模糊查询）", example = "沙县公司")
    private String name;

    @Schema(description = "租户代码（模糊查询）", example = "T001")
    private String code;

    @Schema(description = "租户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}

