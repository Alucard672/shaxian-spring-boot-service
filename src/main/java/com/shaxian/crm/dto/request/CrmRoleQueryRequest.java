package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CRM角色查询请求")
public class CrmRoleQueryRequest {
    @Schema(description = "角色名称（模糊查询）", example = "管理员")
    private String name;

    @Schema(description = "角色代码（模糊查询）", example = "ADMIN")
    private String code;

    @Schema(description = "状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
