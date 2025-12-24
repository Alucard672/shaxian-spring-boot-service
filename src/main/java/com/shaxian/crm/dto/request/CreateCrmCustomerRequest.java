package com.shaxian.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCrmCustomerRequest {
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200")
    private String name;

    private String address;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 50, message = "手机号长度不能超过50")
    private String phone;

    private String remark;

    @NotNull(message = "客户来源不能为空")
    private String source; // ONLINE, OFFLINE, REFERRAL, EXHIBITION, ADVERTISING, OTHER

    private String type; // OFFICIAL, POTENTIAL
}

