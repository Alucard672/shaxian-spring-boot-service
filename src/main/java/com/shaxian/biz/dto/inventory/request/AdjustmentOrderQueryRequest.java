package com.shaxian.biz.dto.inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "库存调整单查询请求")
public class AdjustmentOrderQueryRequest {
    @Schema(description = "调整单状态", example = "COMPLETED", allowableValues = {"DRAFT", "COMPLETED"})
    private String status;
    
    @Schema(description = "调整类型", example = "INCREASE", allowableValues = {"INCREASE", "DECREASE", "SURPLUS", "DEFICIT", "LOSS", "OTHER"})
    private String type;
}
