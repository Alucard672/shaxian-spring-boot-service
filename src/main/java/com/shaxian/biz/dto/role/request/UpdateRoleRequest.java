package com.shaxian.biz.dto.role.request;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String name;

    private String description;

    private String permissions; // JSON 格式存储权限数组
}
