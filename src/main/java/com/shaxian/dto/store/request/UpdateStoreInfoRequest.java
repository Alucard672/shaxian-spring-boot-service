package com.shaxian.dto.store.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateStoreInfoRequest {
    @Size(max = 200, message = "门店名称长度不能超过200")
    private String name;

    @Size(max = 50, message = "门店编码长度不能超过50")
    private String code;

    private String address;

    @Size(max = 50, message = "联系电话长度不能超过50")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 50, message = "传真长度不能超过50")
    private String fax;

    @Size(max = 20, message = "邮编长度不能超过20")
    private String postalCode;

    private String remark;
}
