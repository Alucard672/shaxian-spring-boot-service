package com.shaxian.biz.dto.alert.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpdateInventoryAlertSettingsRequest {
    private Boolean enableAlert;
    private BigDecimal minStockThreshold;
    private BigDecimal maxStockThreshold;
}
