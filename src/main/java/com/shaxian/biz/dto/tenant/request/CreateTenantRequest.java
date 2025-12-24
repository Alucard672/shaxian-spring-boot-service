package com.shaxian.biz.dto.tenant.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {
    @NotBlank(message = "租户名称不能为空")
    private String name;

    @NotBlank(message = "租户地址不能为空")
    private String address;
}

