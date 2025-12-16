package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.sales.SalesAppService;
import com.shaxian.entity.SalesOrder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesAppService salesAppService;

    public SalesController(SalesAppService salesAppService) {
        this.salesAppService = salesAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalesOrder>>> getAllSales(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long cid = customerId != null ? Long.parseLong(customerId) : null;
        List<SalesOrder> orders = salesAppService.listSales(status, cid, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrder>> getSales(@PathVariable Long id) {
        return salesAppService.findById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("销售单不存在")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalesOrder>> createSales(@RequestBody Map<String, Object> request) {
        SalesOrder created = salesAppService.createSales(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrder>> updateSales(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        SalesOrder updated = salesAppService.updateSales(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSales(@PathVariable Long id) {
        salesAppService.deleteSales(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // 原 /check-stock 逻辑可以后续抽到库存模块的 service，这里暂时保留由前端改用统一库存接口
}

