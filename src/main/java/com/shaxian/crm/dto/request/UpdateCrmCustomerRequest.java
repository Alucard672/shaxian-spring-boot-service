package com.shaxian.crm.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCrmCustomerRequest {
    @Size(max = 200, message = "客户名称长度不能超过200")
    private String name;

    private String address;

    @Size(max = 50, message = "手机号长度不能超过50")
    private String phone;

    private String remark;

    private String source; // ONLINE, OFFLINE, REFERRAL, EXHIBITION, ADVERTISING, OTHER

    private String type; // OFFICIAL, POTENTIAL
}

