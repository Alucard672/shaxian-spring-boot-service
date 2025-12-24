package com.shaxian.biz.dto.role.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String description;

    private String permissions; // JSON 格式存储权限数组
}
