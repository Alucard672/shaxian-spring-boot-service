package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新CRM客户请求")
public class UpdateCrmCustomerRequest {
    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", example = "张三")
    private String name;

    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;

    @Size(max = 50, message = "手机号长度不能超过50")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "备注", example = "重要客户")
    private String remark;

    @Schema(description = "客户来源", example = "ONLINE", allowableValues = {"ONLINE", "OFFLINE", "REFERRAL", "EXHIBITION", "ADVERTISING", "OTHER"})
    private String source; // ONLINE, OFFLINE, REFERRAL, EXHIBITION, ADVERTISING, OTHER

    @Schema(description = "客户类型", example = "OFFICIAL", allowableValues = {"OFFICIAL", "POTENTIAL"})
    private String type; // OFFICIAL, POTENTIAL
}

