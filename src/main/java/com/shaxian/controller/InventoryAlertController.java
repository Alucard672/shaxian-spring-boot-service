package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.alert.InventoryAlertAppService;
import com.shaxian.entity.InventoryAlertSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory-alert")
public class InventoryAlertController {

    private final InventoryAlertAppService inventoryAlertAppService;

    public InventoryAlertController(InventoryAlertAppService inventoryAlertAppService) {
        this.inventoryAlertAppService = inventoryAlertAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<InventoryAlertSettings>> getInventoryAlertSettings() {
        InventoryAlertSettings settings = inventoryAlertAppService.getInventoryAlertSettings();
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<InventoryAlertSettings>> updateInventoryAlertSettings(@RequestBody Map<String, Object> request) {
        InventoryAlertSettings settings = inventoryAlertAppService.updateInventoryAlertSettings(request);
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }
}
