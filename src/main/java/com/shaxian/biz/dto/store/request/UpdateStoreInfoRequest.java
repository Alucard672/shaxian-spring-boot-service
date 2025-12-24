package com.shaxian.biz.dto.store.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新门店信息请求")
public class UpdateStoreInfoRequest {
    @Size(max = 200, message = "门店名称长度不能超过200")
    @Schema(description = "门店名称", example = "沙县门店A")
    private String name;

    @Size(max = 50, message = "门店编码长度不能超过50")
    @Schema(description = "门店编码", example = "STORE001")
    private String code;

    @Schema(description = "门店地址", example = "北京市朝阳区")
    private String address;

    @Size(max = 50, message = "联系电话长度不能超过50")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    @Schema(description = "邮箱", example = "store@example.com")
    private String email;

    @Size(max = 50, message = "传真长度不能超过50")
    @Schema(description = "传真", example = "010-12345678")
    private String fax;

    @Size(max = 20, message = "邮编长度不能超过20")
    @Schema(description = "邮编", example = "100000")
    private String postalCode;

    @Schema(description = "备注", example = "总店")
    private String remark;
}
