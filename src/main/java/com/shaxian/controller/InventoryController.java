package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.inventory.InventoryAppService;
import com.shaxian.entity.AdjustmentOrder;
import com.shaxian.entity.InventoryCheckOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryAppService inventoryAppService;

    public InventoryController(InventoryAppService inventoryAppService) {
        this.inventoryAppService = inventoryAppService;
    }

    // ========== 库存调整单 ==========
    @GetMapping("/adjustments")
    public ResponseEntity<ApiResponse<List<AdjustmentOrder>>> getAdjustments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        List<AdjustmentOrder> orders = inventoryAppService.listAdjustments(status, type);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/adjustments/{id}")
    public ResponseEntity<ApiResponse<AdjustmentOrder>> getAdjustment(@PathVariable Long id) {
        return inventoryAppService.findAdjustment(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("库存调整单不存在")));
    }

    @PostMapping("/adjustments")
    public ResponseEntity<ApiResponse<AdjustmentOrder>> createAdjustment(@RequestBody Map<String, Object> request) {
        AdjustmentOrder created = inventoryAppService.createAdjustment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/adjustments/{id}")
    public ResponseEntity<ApiResponse<AdjustmentOrder>> updateAdjustment(@PathVariable Long id,
                                                                         @RequestBody Map<String, Object> request) {
        AdjustmentOrder updated = inventoryAppService.updateAdjustment(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    // ========== 盘点单 ==========
    @GetMapping("/checks")
    public ResponseEntity<ApiResponse<List<InventoryCheckOrder>>> getChecks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warehouse) {
        List<InventoryCheckOrder> orders = inventoryAppService.listChecks(status, warehouse);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/checks/{id}")
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> getCheck(@PathVariable Long id) {
        return inventoryAppService.findCheck(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("盘点单不存在")));
    }

    @PostMapping("/checks")
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> createCheck(@RequestBody Map<String, Object> request) {
        InventoryCheckOrder created = inventoryAppService.createCheck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/checks/{id}")
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> updateCheck(@PathVariable Long id,
                                                                        @RequestBody Map<String, Object> request) {
        InventoryCheckOrder updated = inventoryAppService.updateCheck(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/checks/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCheck(@PathVariable Long id) {
        inventoryAppService.deleteCheck(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}
