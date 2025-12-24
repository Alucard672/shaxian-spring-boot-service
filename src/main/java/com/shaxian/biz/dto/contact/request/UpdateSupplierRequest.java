package com.shaxian.biz.dto.contact.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新供应商请求")
public class UpdateSupplierRequest {
    @Size(max = 200, message = "供应商名称长度不能超过200")
    @Schema(description = "供应商名称", example = "供应商A")
    private String name;

    @Size(max = 50, message = "供应商编码长度不能超过50")
    @Schema(description = "供应商编码", example = "SUP001")
    private String code;

    @Size(max = 100, message = "联系人长度不能超过100")
    @Schema(description = "联系人", example = "李四")
    private String contactPerson;

    @Size(max = 50, message = "联系电话长度不能超过50")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "地址", example = "上海市浦东新区")
    private String address;

    @Schema(description = "供应商类型", example = "MANUFACTURER", allowableValues = {"MANUFACTURER", "TRADER"})
    private String type; // MANUFACTURER, TRADER

    @Schema(description = "结算周期", example = "MONTHLY", allowableValues = {"CASH", "MONTHLY", "QUARTERLY"})
    private String settlementCycle; // CASH, MONTHLY, QUARTERLY

    @Schema(description = "供应商状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status; // ACTIVE, INACTIVE

    @Schema(description = "备注", example = "长期合作供应商")
    private String remark;
}
