package com.shaxian.biz.dto.role.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建角色请求")
public class CreateRoleRequest {
    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", required = true, example = "管理员")
    private String name;

    @Schema(description = "角色描述", example = "系统管理员角色")
    private String description;

    @Schema(description = "权限列表（JSON格式）", example = "[\"user:read\", \"user:write\"]")
    private String permissions; // JSON 格式存储权限数组
}
