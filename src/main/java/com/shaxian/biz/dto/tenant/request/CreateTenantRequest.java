package com.shaxian.biz.dto.tenant.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "创建租户请求")
public class CreateTenantRequest {
    @NotBlank(message = "租户名称不能为空")
    @Schema(description = "租户名称", required = true, example = "沙县公司")
    private String name;

    @NotBlank(message = "租户地址不能为空")
    @Schema(description = "租户地址", required = true, example = "北京市朝阳区")
    private String address;

    @Schema(description = "初始有效期；管理端创建时必填", example = "2027-05-12T00:00:00")
    private LocalDateTime expiresAt;

    @Schema(description = "套餐 ID；不填默认绑定'标准版'")
    private Long packageId;

    @Schema(description = "业务员归属（关联 users.id，预留字段，本期可不填）")
    private Long assignedUserId;
}

