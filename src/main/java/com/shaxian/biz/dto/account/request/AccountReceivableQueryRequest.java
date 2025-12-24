package com.shaxian.biz.dto.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应收账款查询请求")
public class AccountReceivableQueryRequest {
    @Schema(description = "客户ID", example = "1")
    private String customerId;
    
    @Schema(description = "收款状态", example = "UNPAID", allowableValues = {"UNPAID", "PARTIALLY_PAID", "PAID"})
    private String status;
}
