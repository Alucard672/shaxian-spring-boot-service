package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CRM客户查询请求")
public class CrmCustomerQueryRequest {
    @Schema(description = "客户名称（模糊查询）", example = "张三")
    private String name;

    @Schema(description = "手机号（模糊查询）", example = "13800138000")
    private String phone;

    @Schema(description = "客户来源", example = "ONLINE", allowableValues = {"ONLINE", "OFFLINE", "REFERRAL", "EXHIBITION", "ADVERTISING", "OTHER"})
    private String source;

    @Schema(description = "客户类型", example = "OFFICIAL", allowableValues = {"OFFICIAL", "POTENTIAL"})
    private String type;
}

