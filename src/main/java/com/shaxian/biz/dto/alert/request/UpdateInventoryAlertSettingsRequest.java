package com.shaxian.biz.dto.alert.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import lombok.Data;

@Data
@Schema(description = "更新库存告警设置请求")
public class UpdateInventoryAlertSettingsRequest {
    @Schema(description = "是否启用告警", example = "true")
    private Boolean enableAlert;
    
    @Schema(description = "最小库存阈值", example = "100.00")
    private BigDecimal minStockThreshold;
    
    @Schema(description = "最大库存阈值", example = "10000.00")
    private BigDecimal maxStockThreshold;
}
