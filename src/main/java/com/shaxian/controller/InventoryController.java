package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.inventory.InventoryAppService;
import com.shaxian.auth.UserSession;
import com.shaxian.entity.AdjustmentOrder;
import com.shaxian.entity.InventoryCheckOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "库存管理", description = "库存调整单、盘点单管理接口")
public class InventoryController {

    private final InventoryAppService inventoryAppService;

    public InventoryController(InventoryAppService inventoryAppService) {
        this.inventoryAppService = inventoryAppService;
    }

    // ========== 库存调整单 ==========
    @GetMapping("/adjustments")
    @Operation(summary = "获取库存调整单列表", description = "查询库存调整单，支持按状态和类型筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取库存调整单列表")
    })
    public ResponseEntity<ApiResponse<List<AdjustmentOrder>>> getAdjustments(
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            UserSession session) {
        List<AdjustmentOrder> orders = inventoryAppService.listAdjustments(status, type);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/adjustments/{id}")
    @Operation(summary = "获取库存调整单详情", description = "根据ID获取库存调整单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取库存调整单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "库存调整单不存在")
    })
    public ResponseEntity<ApiResponse<AdjustmentOrder>> getAdjustment(
            @Parameter(description = "库存调整单ID", required = true) @PathVariable Long id,
            UserSession session) {
        return inventoryAppService.findAdjustment(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("库存调整单不存在")));
    }

    @PostMapping("/adjustments")
    @Operation(summary = "创建库存调整单", description = "创建新的库存调整单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建库存调整单")
    })
    public ResponseEntity<ApiResponse<AdjustmentOrder>> createAdjustment(
            @RequestBody Map<String, Object> request,
            UserSession session) {
        AdjustmentOrder created = inventoryAppService.createAdjustment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/adjustments/{id}")
    @Operation(summary = "更新库存调整单", description = "更新库存调整单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新库存调整单")
    })
    public ResponseEntity<ApiResponse<AdjustmentOrder>> updateAdjustment(
            @Parameter(description = "库存调整单ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            UserSession session) {
        AdjustmentOrder updated = inventoryAppService.updateAdjustment(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    // ========== 盘点单 ==========
    @GetMapping("/checks")
    @Operation(summary = "获取盘点单列表", description = "查询盘点单，支持按状态和仓库筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取盘点单列表")
    })
    public ResponseEntity<ApiResponse<List<InventoryCheckOrder>>> getChecks(
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "仓库") @RequestParam(required = false) String warehouse,
            UserSession session) {
        List<InventoryCheckOrder> orders = inventoryAppService.listChecks(status, warehouse);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/checks/{id}")
    @Operation(summary = "获取盘点单详情", description = "根据ID获取盘点单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取盘点单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "盘点单不存在")
    })
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> getCheck(
            @Parameter(description = "盘点单ID", required = true) @PathVariable Long id,
            UserSession session) {
        return inventoryAppService.findCheck(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("盘点单不存在")));
    }

    @PostMapping("/checks")
    @Operation(summary = "创建盘点单", description = "创建新的盘点单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建盘点单")
    })
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> createCheck(
            @RequestBody Map<String, Object> request,
            UserSession session) {
        InventoryCheckOrder created = inventoryAppService.createCheck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/checks/{id}")
    @Operation(summary = "更新盘点单", description = "更新盘点单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新盘点单")
    })
    public ResponseEntity<ApiResponse<InventoryCheckOrder>> updateCheck(
            @Parameter(description = "盘点单ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            UserSession session) {
        InventoryCheckOrder updated = inventoryAppService.updateCheck(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/checks/{id}")
    @Operation(summary = "删除盘点单", description = "删除指定盘点单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除盘点单")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCheck(
            @Parameter(description = "盘点单ID", required = true) @PathVariable Long id,
            UserSession session) {
        inventoryAppService.deleteCheck(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}
