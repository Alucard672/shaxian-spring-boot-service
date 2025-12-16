package com.shaxian.dto.contact.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCustomerRequest {
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200")
    private String name;

    @NotBlank(message = "客户编码不能为空")
    @Size(max = 50, message = "客户编码长度不能超过50")
    private String code;

    @Size(max = 100, message = "联系人长度不能超过100")
    private String contactPerson;

    @Size(max = 50, message = "联系电话长度不能超过50")
    private String phone;

    private String address;

    private String type; // DIRECT, DEALER

    private BigDecimal creditLimit;

    private String status; // NORMAL, FROZEN

    private String remark;
}
