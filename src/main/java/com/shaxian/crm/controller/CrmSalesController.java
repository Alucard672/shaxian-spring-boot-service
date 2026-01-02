package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.appservice.CrmSalesAppService;
import com.shaxian.crm.dto.request.CreateCrmSalesOrderRequest;
import com.shaxian.crm.dto.request.CrmSalesOrderQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmSalesOrderRequest;
import com.shaxian.crm.entity.CrmSalesOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/sales")
@Tag(name = "CRM销售管理", description = "软件销售订单管理接口")
public class CrmSalesController {

    private final CrmSalesAppService crmSalesAppService;

    public CrmSalesController(CrmSalesAppService crmSalesAppService) {
        this.crmSalesAppService = crmSalesAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询软件销售订单列表", description = "分页查询软件销售订单，支持按客户ID、状态、日期范围等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取订单列表")
    })
    public ResponseEntity<ApiResponse<PageResult<CrmSalesOrder>>> querySalesOrders(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmSalesOrderQueryRequest request,
            UserSession session) {
        if (request == null) {
            request = new CrmSalesOrderQueryRequest();
        }
        PageResult<CrmSalesOrder> result = crmSalesAppService.querySalesOrders(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取软件销售订单详情", description = "根据ID获取软件销售订单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取订单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public ResponseEntity<ApiResponse<CrmSalesOrder>> getSalesOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            UserSession session) {
        return crmSalesAppService.getSalesOrder(id)
                .map(order -> ResponseEntity.ok(ApiResponse.ok(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("订单不存在")));
    }

    @PostMapping
    @Operation(summary = "创建软件销售订单", description = "创建新的软件销售订单，如果客户是潜在客户且没有租户，将自动创建租户并更新客户状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建订单")
    })
    public ResponseEntity<ApiResponse<CrmSalesOrder>> createSalesOrder(
            @Valid @RequestBody CreateCrmSalesOrderRequest request,
            UserSession session) {
        CrmSalesOrder created = crmSalesAppService.createSalesOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新软件销售订单", description = "更新软件销售订单信息（仅草稿状态可更新）")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新订单")
    })
    public ResponseEntity<ApiResponse<CrmSalesOrder>> updateSalesOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCrmSalesOrderRequest request,
            UserSession session) {
        CrmSalesOrder updated = crmSalesAppService.updateSalesOrder(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除软件销售订单", description = "删除指定软件销售订单（仅草稿状态可删除）")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除订单")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSalesOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            UserSession session) {
        crmSalesAppService.deleteSalesOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

