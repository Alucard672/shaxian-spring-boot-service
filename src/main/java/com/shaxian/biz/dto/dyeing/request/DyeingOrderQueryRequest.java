package com.shaxian.biz.dto.dyeing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "染色订单查询请求")
public class DyeingOrderQueryRequest {
    @Schema(description = "订单状态", example = "PROCESSING", allowableValues = {"PENDING_SHIPMENT", "PROCESSING", "COMPLETED", "RECEIVED", "CANCELLED"})
    private String status;
    
    @Schema(description = "商品ID", example = "1")
    private Long productId;
}
