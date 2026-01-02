package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "软件销售订单明细请求")
public class CrmSalesOrderItemRequest {
    @NotNull(message = "产品ID不能为空")
    @Schema(description = "产品ID", required = true, example = "1")
    private Long productId;

    @Size(max = 200, message = "产品名称长度不能超过200")
    @Schema(description = "产品名称", example = "软件授权")
    private String productName;

    @Size(max = 50, message = "产品编码长度不能超过50")
    @Schema(description = "产品编码", example = "PROD001")
    private String productCode;

    @NotNull(message = "单价不能为空")
    @Schema(description = "单价", required = true, example = "1000.00")
    private BigDecimal unitPrice;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", required = true, example = "1")
    private Integer quantity;

    @Schema(description = "备注", example = "年度授权")
    private String remark;
}

