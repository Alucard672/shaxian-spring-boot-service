package com.shaxian.biz.dto.tenant.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建租户请求")
public class CreateTenantRequest {
    @NotBlank(message = "租户名称不能为空")
    @Schema(description = "租户名称", required = true, example = "沙县公司")
    private String name;

    @NotBlank(message = "租户地址不能为空")
    @Schema(description = "租户地址", required = true, example = "北京市朝阳区")
    private String address;
}

