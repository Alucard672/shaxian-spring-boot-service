package com.shaxian.dto.alert.request;

import lombok.Data;

@Data
public class UpdateInventoryAlertSettingsRequest {
    private Boolean enableAlert;
    private BigDecimal minStockThreshold;
    private BigDecimal maxStockThreshold;
}
