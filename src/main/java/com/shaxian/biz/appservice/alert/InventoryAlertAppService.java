package com.shaxian.biz.appservice.alert;

import com.shaxian.biz.entity.InventoryAlertSettings;
import com.shaxian.biz.service.settings.InventoryAlertSettingsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryAlertAppService {

    private final InventoryAlertSettingsService inventoryAlertSettingsService;

    public InventoryAlertAppService(InventoryAlertSettingsService inventoryAlertSettingsService) {
        this.inventoryAlertSettingsService = inventoryAlertSettingsService;
    }

    public InventoryAlertSettings getInventoryAlertSettings() {
        return inventoryAlertSettingsService.getSettings();
    }

    public InventoryAlertSettings updateInventoryAlertSettings(Map<String, Object> request) {
        return inventoryAlertSettingsService.updateSettings(request);
    }
}
