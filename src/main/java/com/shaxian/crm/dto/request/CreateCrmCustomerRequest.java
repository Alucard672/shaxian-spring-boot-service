package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建CRM客户请求")
public class CreateCrmCustomerRequest {
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", required = true, example = "张三")
    private String name;

    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 50, message = "手机号长度不能超过50")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @Schema(description = "备注", example = "重要客户")
    private String remark;

    @NotNull(message = "客户来源不能为空")
    @Schema(description = "客户来源", required = true, example = "ONLINE", allowableValues = {"ONLINE", "OFFLINE", "REFERRAL", "EXHIBITION", "ADVERTISING", "OTHER"})
    private String source; // ONLINE, OFFLINE, REFERRAL, EXHIBITION, ADVERTISING, OTHER

    @Schema(description = "客户类型", example = "OFFICIAL", allowableValues = {"OFFICIAL", "POTENTIAL"})
    private String type; // OFFICIAL, POTENTIAL
}

