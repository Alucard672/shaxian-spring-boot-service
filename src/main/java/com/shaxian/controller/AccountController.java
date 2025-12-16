package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.account.AccountAppService;
import com.shaxian.entity.AccountPayable;
import com.shaxian.entity.AccountReceivable;
import com.shaxian.entity.PaymentRecord;
import com.shaxian.entity.ReceiptRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountAppService accountAppService;

    public AccountController(AccountAppService accountAppService) {
        this.accountAppService = accountAppService;
    }

    // ========== 应收账款 ==========
    @GetMapping("/receivables")
    public ResponseEntity<ApiResponse<List<AccountReceivable>>> getReceivables(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status) {
        List<AccountReceivable> list = accountAppService.listReceivables(customerId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/receivables")
    public ResponseEntity<ApiResponse<AccountReceivable>> createReceivable(@RequestBody Map<String, Object> request) {
        AccountReceivable created = accountAppService.createReceivable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/receivables/{id}/receipts")
    public ResponseEntity<ApiResponse<List<ReceiptRecord>>> getReceipts(@PathVariable Long id) {
        List<ReceiptRecord> list = accountAppService.listReceipts(id);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/receivables/{id}/receipts")
    public ResponseEntity<ApiResponse<ReceiptRecord>> createReceipt(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        ReceiptRecord created = accountAppService.createReceipt(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    // ========== 应付账款 ==========
    @GetMapping("/payables")
    public ResponseEntity<ApiResponse<List<AccountPayable>>> getPayables(
            @RequestParam(required = false) String supplierId,
            @RequestParam(required = false) String status) {
        List<AccountPayable> list = accountAppService.listPayables(supplierId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/payables")
    public ResponseEntity<ApiResponse<AccountPayable>> createPayable(@RequestBody Map<String, Object> request) {
        AccountPayable created = accountAppService.createPayable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/payables/{id}/payments")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPayments(@PathVariable Long id) {
        List<PaymentRecord> list = accountAppService.listPayments(id);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/payables/{id}/payments")
    public ResponseEntity<ApiResponse<PaymentRecord>> createPayment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        PaymentRecord created = accountAppService.createPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }
}

