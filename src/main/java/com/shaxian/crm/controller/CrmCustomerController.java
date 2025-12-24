package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.appservice.CrmCustomerAppService;
import com.shaxian.crm.dto.request.CreateCrmCustomerRequest;
import com.shaxian.crm.dto.request.CrmCustomerQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmCustomerRequest;
import com.shaxian.crm.entity.CrmCustomer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/customers")
@Tag(name = "CRM客户管理", description = "软件销售客户管理接口")
public class CrmCustomerController {

    private final CrmCustomerAppService crmCustomerAppService;

    public CrmCustomerController(CrmCustomerAppService crmCustomerAppService) {
        this.crmCustomerAppService = crmCustomerAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询CRM客户列表", description = "分页查询CRM客户，支持按名称、手机号、来源、类型等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取客户列表")
    })
    public ResponseEntity<ApiResponse<PageResult<CrmCustomer>>> queryCustomers(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmCustomerQueryRequest request,
            UserSession session) {
        if (request == null) {
            request = new CrmCustomerQueryRequest();
        }
        PageResult<CrmCustomer> result = crmCustomerAppService.queryCustomers(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取CRM客户详情", description = "根据ID获取CRM客户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取客户信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "客户不存在")
    })
    public ResponseEntity<ApiResponse<CrmCustomer>> getCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            UserSession session) {
        return crmCustomerAppService.findCustomer(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.ok(customer)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("客户不存在")));
    }

    @PostMapping
    @Operation(summary = "创建CRM客户", description = "创建新CRM客户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建客户")
    })
    public ResponseEntity<ApiResponse<CrmCustomer>> createCustomer(
            @Valid @RequestBody CreateCrmCustomerRequest request,
            UserSession session) {
        CrmCustomer created = crmCustomerAppService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新CRM客户", description = "更新CRM客户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新客户")
    })
    public ResponseEntity<ApiResponse<CrmCustomer>> updateCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCrmCustomerRequest request,
            UserSession session) {
        CrmCustomer updated = crmCustomerAppService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除CRM客户", description = "删除指定CRM客户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除客户")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            UserSession session) {
        crmCustomerAppService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

