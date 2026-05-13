package com.shaxian.biz.dto.tenant.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "更新租户请求")
public class UpdateTenantRequest {
    @Size(max = 200, message = "租户名称长度不能超过200")
    @Schema(description = "租户名称", example = "沙县公司")
    private String name;

    @Schema(description = "租户地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "租户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Schema(description = "到期时间（管理员手工调整）")
    private LocalDateTime expiresAt;

    @Schema(description = "套餐 ID")
    private Long packageId;

    @Schema(description = "业务员归属（预留）")
    private Long assignedUserId;
}

