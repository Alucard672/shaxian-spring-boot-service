package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.purchase.PurchaseAppService;
import com.shaxian.entity.PurchaseOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "采购管理", description = "采购订单管理接口")
public class PurchaseController {

    private final PurchaseAppService purchaseAppService;

    public PurchaseController(PurchaseAppService purchaseAppService) {
        this.purchaseAppService = purchaseAppService;
    }

    @GetMapping
    @Operation(summary = "获取采购订单列表", description = "查询采购订单，支持按状态、供应商和日期范围筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取采购订单列表")
    })
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getAllPurchases(
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestBody(required = false) com.shaxian.dto.purchase.request.PurchaseOrderQueryRequest request) {
        String status = request != null ? request.getStatus() : null;
        Long supplierId = request != null ? request.getSupplierId() : null;
        LocalDate startDate = request != null ? request.getStartDate() : null;
        LocalDate endDate = request != null ? request.getEndDate() : null;
        List<PurchaseOrder> orders = purchaseAppService.listPurchases(status, supplierId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取采购订单详情", description = "根据ID获取采购订单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取采购订单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "采购订单不存在")
    })
    public ResponseEntity<ApiResponse<PurchaseOrder>> getPurchase(
            @Parameter(description = "采购订单ID", required = true) @PathVariable Long id) {
        return purchaseAppService.findById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("进货单不存在")));
    }

    @PostMapping
    @Operation(summary = "创建采购订单", description = "创建新的采购订单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建采购订单")
    })
    public ResponseEntity<ApiResponse<PurchaseOrder>> createPurchase(@RequestBody com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest request) {
        PurchaseOrder created = purchaseAppService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新采购订单", description = "更新采购订单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新采购订单")
    })
    public ResponseEntity<ApiResponse<PurchaseOrder>> updatePurchase(
            @Parameter(description = "采购订单ID", required = true) @PathVariable Long id,
            @RequestBody com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest request) {
        PurchaseOrder updated = purchaseAppService.updatePurchase(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除采购订单", description = "删除指定采购订单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除采购订单")
    })
    public ResponseEntity<ApiResponse<Void>> deletePurchase(
            @Parameter(description = "采购订单ID", required = true) @PathVariable Long id) {
        purchaseAppService.deletePurchase(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

