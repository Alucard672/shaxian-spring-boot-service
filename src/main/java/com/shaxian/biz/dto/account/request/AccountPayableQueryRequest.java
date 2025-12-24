package com.shaxian.biz.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应付账款查询请求")
public class AccountPayableQueryRequest {
    @Schema(description = "供应商ID", example = "1")
    private String supplierId;
    
    @Schema(description = "付款状态", example = "UNPAID", allowableValues = {"UNPAID", "PARTIALLY_PAID", "PAID"})
    private String status;
}
