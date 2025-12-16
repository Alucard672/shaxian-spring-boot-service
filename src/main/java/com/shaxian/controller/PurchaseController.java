package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.purchase.PurchaseAppService;
import com.shaxian.entity.PurchaseOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseAppService purchaseAppService;

    public PurchaseController(PurchaseAppService purchaseAppService) {
        this.purchaseAppService = purchaseAppService;
    }

    @GetMapping
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
    public ResponseEntity<ApiResponse<PurchaseOrder>> getPurchase(@PathVariable Long id) {
        return purchaseAppService.findById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("进货单不存在")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrder>> createPurchase(@RequestBody com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest request) {
        PurchaseOrder created = purchaseAppService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrder>> updatePurchase(@PathVariable Long id, @RequestBody com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest request) {
        PurchaseOrder updated = purchaseAppService.updatePurchase(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase(@PathVariable Long id) {
        purchaseAppService.deletePurchase(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

