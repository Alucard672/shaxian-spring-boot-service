package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CRM用户查询请求")
public class CrmUserQueryRequest {
    @Schema(description = "状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Schema(description = "手机号（模糊查询）", example = "13800138000")
    private String phone;

    @Schema(description = "姓名（模糊查询）", example = "张三")
    private String name;

    @Schema(description = "邮箱（模糊查询）", example = "zhangsan@example.com")
    private String email;
}

