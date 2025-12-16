package com.shaxian.appservice.alert;

import com.shaxian.entity.InventoryAlertSettings;
import com.shaxian.service.settings.InventoryAlertSettingsService;
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
