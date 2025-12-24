package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.account.AccountAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.AccountPayable;
import com.shaxian.biz.entity.AccountReceivable;
import com.shaxian.biz.entity.PaymentRecord;
import com.shaxian.biz.entity.ReceiptRecord;
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
@RequestMapping("/api/accounts")
@Tag(name = "账户管理", description = "应收账款、应付账款管理接口")
public class AccountController {

    private final AccountAppService accountAppService;

    public AccountController(AccountAppService accountAppService) {
        this.accountAppService = accountAppService;
    }

    // ========== 应收账款 ==========
    @GetMapping("/receivables")
    @Operation(summary = "获取应收账款列表", description = "查询应收账款，支持按客户ID和状态筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取应收账款列表")
    })
    public ResponseEntity<ApiResponse<List<AccountReceivable>>> getReceivables(
            @Parameter(description = "客户ID") @RequestParam(required = false) String customerId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            UserSession session) {
        List<AccountReceivable> list = accountAppService.listReceivables(customerId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/receivables")
    @Operation(summary = "创建应收账款", description = "创建新的应收账款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建应收账款")
    })
    public ResponseEntity<ApiResponse<AccountReceivable>> createReceivable(
            @RequestBody Map<String, Object> request,
            UserSession session) {
        AccountReceivable created = accountAppService.createReceivable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/receivables/{id}/receipts")
    @Operation(summary = "获取收款记录列表", description = "获取指定应收账款的收款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取收款记录列表")
    })
    public ResponseEntity<ApiResponse<List<ReceiptRecord>>> getReceipts(
            @Parameter(description = "应收账款ID", required = true) @PathVariable Long id,
            UserSession session) {
        List<ReceiptRecord> list = accountAppService.listReceipts(id);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/receivables/{id}/receipts")
    @Operation(summary = "创建收款记录", description = "为指定应收账款创建收款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建收款记录")
    })
    public ResponseEntity<ApiResponse<ReceiptRecord>> createReceipt(
            @Parameter(description = "应收账款ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            UserSession session) {
        ReceiptRecord created = accountAppService.createReceipt(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    // ========== 应付账款 ==========
    @GetMapping("/payables")
    @Operation(summary = "获取应付账款列表", description = "查询应付账款，支持按供应商ID和状态筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取应付账款列表")
    })
    public ResponseEntity<ApiResponse<List<AccountPayable>>> getPayables(
            @Parameter(description = "供应商ID") @RequestParam(required = false) String supplierId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            UserSession session) {
        List<AccountPayable> list = accountAppService.listPayables(supplierId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/payables")
    @Operation(summary = "创建应付账款", description = "创建新的应付账款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建应付账款")
    })
    public ResponseEntity<ApiResponse<AccountPayable>> createPayable(
            @RequestBody Map<String, Object> request,
            UserSession session) {
        AccountPayable created = accountAppService.createPayable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/payables/{id}/payments")
    @Operation(summary = "获取付款记录列表", description = "获取指定应付账款的付款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取付款记录列表")
    })
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPayments(
            @Parameter(description = "应付账款ID", required = true) @PathVariable Long id,
            UserSession session) {
        List<PaymentRecord> list = accountAppService.listPayments(id);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/payables/{id}/payments")
    @Operation(summary = "创建付款记录", description = "为指定应付账款创建付款记录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建付款记录")
    })
    public ResponseEntity<ApiResponse<PaymentRecord>> createPayment(
            @Parameter(description = "应付账款ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            UserSession session) {
        PaymentRecord created = accountAppService.createPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }
}

