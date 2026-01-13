package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新CRM角色请求")
public class UpdateCrmRoleRequest {
    @Size(max = 100, message = "角色名称长度不能超过100")
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    @Size(max = 50, message = "角色代码长度不能超过50")
    @Schema(description = "角色代码", example = "ADMIN")
    private String code;

    @Schema(description = "角色级别，数值越大级别越高", example = "10")
    private Integer level;

    @Schema(description = "描述", example = "系统管理员角色")
    private String description;

    @Schema(description = "状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
