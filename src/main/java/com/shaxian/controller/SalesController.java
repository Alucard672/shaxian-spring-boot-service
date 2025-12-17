package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.sales.SalesAppService;
import com.shaxian.entity.SalesOrder;
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
@RequestMapping("/api/sales")
@Tag(name = "销售管理", description = "销售订单管理接口")
public class SalesController {

    private final SalesAppService salesAppService;

    public SalesController(SalesAppService salesAppService) {
        this.salesAppService = salesAppService;
    }

    @GetMapping
    @Operation(summary = "获取销售订单列表", description = "查询销售订单，支持按状态、客户和日期范围筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取销售订单列表")
    })
    public ResponseEntity<ApiResponse<List<SalesOrder>>> getAllSales(
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestBody(required = false) com.shaxian.dto.sales.request.SalesOrderQueryRequest request) {
        String status = request != null ? request.getStatus() : null;
        Long customerId = request != null ? request.getCustomerId() : null;
        LocalDate startDate = request != null ? request.getStartDate() : null;
        LocalDate endDate = request != null ? request.getEndDate() : null;
        List<SalesOrder> orders = salesAppService.listSales(status, customerId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取销售订单详情", description = "根据ID获取销售订单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取销售订单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "销售订单不存在")
    })
    public ResponseEntity<ApiResponse<SalesOrder>> getSales(
            @Parameter(description = "销售订单ID", required = true) @PathVariable Long id) {
        return salesAppService.findById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("销售单不存在")));
    }

    @PostMapping
    @Operation(summary = "创建销售订单", description = "创建新的销售订单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建销售订单")
    })
    public ResponseEntity<ApiResponse<SalesOrder>> createSales(@RequestBody com.shaxian.dto.sales.request.CreateSalesOrderRequest request) {
        SalesOrder created = salesAppService.createSales(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新销售订单", description = "更新销售订单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新销售订单")
    })
    public ResponseEntity<ApiResponse<SalesOrder>> updateSales(
            @Parameter(description = "销售订单ID", required = true) @PathVariable Long id,
            @RequestBody com.shaxian.dto.sales.request.UpdateSalesOrderRequest request) {
        SalesOrder updated = salesAppService.updateSales(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除销售订单", description = "删除指定销售订单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除销售订单")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSales(
            @Parameter(description = "销售订单ID", required = true) @PathVariable Long id) {
        salesAppService.deleteSales(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // 原 /check-stock 逻辑可以后续抽到库存模块的 service，这里暂时保留由前端改用统一库存接口
}

