package com.shaxian.biz.dto.contact.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新客户请求")
public class UpdateCustomerRequest {
    @Size(max = 200, message = "客户名称长度不能超过200")
    @Schema(description = "客户名称", example = "张三公司")
    private String name;

    @Size(max = 50, message = "客户编码长度不能超过50")
    @Schema(description = "客户编码", example = "CUST001")
    private String code;

    @Size(max = 100, message = "联系人长度不能超过100")
    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Size(max = 50, message = "联系电话长度不能超过50")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "客户类型", example = "DIRECT", allowableValues = {"DIRECT", "DEALER"})
    private String type; // DIRECT, DEALER

    @Schema(description = "信用额度", example = "100000.00")
    private BigDecimal creditLimit;

    @Schema(description = "客户状态", example = "NORMAL", allowableValues = {"NORMAL", "FROZEN"})
    private String status; // NORMAL, FROZEN

    @Schema(description = "备注", example = "重要客户")
    private String remark;
}
