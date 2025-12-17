package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.alert.InventoryAlertAppService;
import com.shaxian.entity.InventoryAlertSettings;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory-alert")
@Tag(name = "库存预警", description = "库存预警设置管理接口")
public class InventoryAlertController {

    private final InventoryAlertAppService inventoryAlertAppService;

    public InventoryAlertController(InventoryAlertAppService inventoryAlertAppService) {
        this.inventoryAlertAppService = inventoryAlertAppService;
    }

    @GetMapping
    @Operation(summary = "获取库存预警设置", description = "获取当前库存预警配置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取库存预警设置")
    })
    public ResponseEntity<ApiResponse<InventoryAlertSettings>> getInventoryAlertSettings() {
        InventoryAlertSettings settings = inventoryAlertAppService.getInventoryAlertSettings();
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }

    @PutMapping
    @Operation(summary = "更新库存预警设置", description = "更新库存预警配置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新库存预警设置")
    })
    public ResponseEntity<ApiResponse<InventoryAlertSettings>> updateInventoryAlertSettings(@RequestBody Map<String, Object> request) {
        InventoryAlertSettings settings = inventoryAlertAppService.updateInventoryAlertSettings(request);
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }
}
