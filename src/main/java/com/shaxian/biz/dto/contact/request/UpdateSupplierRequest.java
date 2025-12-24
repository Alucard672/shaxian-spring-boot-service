package com.shaxian.biz.dto.contact.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSupplierRequest {
    @Size(max = 200, message = "供应商名称长度不能超过200")
    private String name;

    @Size(max = 50, message = "供应商编码长度不能超过50")
    private String code;

    @Size(max = 100, message = "联系人长度不能超过100")
    private String contactPerson;

    @Size(max = 50, message = "联系电话长度不能超过50")
    private String phone;

    private String address;

    private String type; // MANUFACTURER, TRADER

    private String settlementCycle; // CASH, MONTHLY, QUARTERLY

    private String status; // ACTIVE, INACTIVE

    private String remark;
}
