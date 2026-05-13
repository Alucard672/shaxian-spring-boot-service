package com.shaxian.biz.dto.admin.request;

import com.shaxian.biz.dto.tenant.request.TenantQueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理端租户查询请求（扩展过滤项）")
public class TenantAdminQueryRequest extends TenantQueryRequest {
    @Schema(description = "套餐 ID 过滤")
    private Long packageId;

    @Schema(description = "业务员归属 ID 过滤")
    private Long assignedUserId;

    @Schema(description = "X 天内到期；不填则不限")
    private Integer expiringDays;
}
