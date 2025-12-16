package com.shaxian.service.settings;

import com.shaxian.entity.InventoryAlertSettings;
import com.shaxian.repository.InventoryAlertSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryAlertSettingsService {

    private final InventoryAlertSettingsRepository inventoryAlertSettingsRepository;

    public InventoryAlertSettingsService(InventoryAlertSettingsRepository inventoryAlertSettingsRepository) {
        this.inventoryAlertSettingsRepository = inventoryAlertSettingsRepository;
    }

    public InventoryAlertSettings getSettings() {
        Optional<InventoryAlertSettings> settings = inventoryAlertSettingsRepository.findFirstByOrderByIdAsc();
        if (settings.isEmpty()) {
            InventoryAlertSettings newSettings = new InventoryAlertSettings();
            return inventoryAlertSettingsRepository.save(newSettings);
        }
        return settings.get();
    }

    @Transactional
    public InventoryAlertSettings updateSettings(Map<String, Object> request) {
        Optional<InventoryAlertSettings> existing = inventoryAlertSettingsRepository.findFirstByOrderByIdAsc();
        InventoryAlertSettings settings;

        if (existing.isPresent()) {
            settings = existing.get();
        } else {
            settings = new InventoryAlertSettings();
        }

        if (request.containsKey("enabled")) {
            settings.setEnabled((Boolean) request.get("enabled"));
        }
        if (request.containsKey("threshold")) {
            settings.setThreshold(new BigDecimal(request.get("threshold").toString()));
        }
        if (request.containsKey("autoAlert")) {
            settings.setAutoAlert((Boolean) request.get("autoAlert"));
        }

        return inventoryAlertSettingsRepository.save(settings);
    }
}
