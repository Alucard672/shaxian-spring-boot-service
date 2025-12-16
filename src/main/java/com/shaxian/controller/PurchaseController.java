package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.purchase.PurchaseAppService;
import com.shaxian.entity.PurchaseOrder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseAppService purchaseAppService;

    public PurchaseController(PurchaseAppService purchaseAppService) {
        this.purchaseAppService = purchaseAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getAllPurchases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long supplier = supplierId != null ? Long.parseLong(supplierId) : null;
        List<PurchaseOrder> orders = purchaseAppService.listPurchases(status, supplier, startDate, endDate);
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
    public ResponseEntity<ApiResponse<PurchaseOrder>> createPurchase(@RequestBody Map<String, Object> request) {
        PurchaseOrder created = purchaseAppService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrder>> updatePurchase(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        PurchaseOrder updated = purchaseAppService.updatePurchase(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase(@PathVariable Long id) {
        purchaseAppService.deletePurchase(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

