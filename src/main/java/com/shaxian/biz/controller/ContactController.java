package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.contact.ContactAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.contact.request.CreateCustomerRequest;
import com.shaxian.biz.dto.contact.request.CreateSupplierRequest;
import com.shaxian.biz.dto.contact.request.UpdateCustomerRequest;
import com.shaxian.biz.dto.contact.request.UpdateSupplierRequest;
import com.shaxian.biz.entity.Customer;
import com.shaxian.biz.entity.Supplier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz/api/contacts")
@Tag(name = "联系人管理", description = "客户、供应商管理接口")
public class ContactController {

    private final ContactAppService contactAppService;

    public ContactController(ContactAppService contactAppService) {
        this.contactAppService = contactAppService;
    }

    // ========== 客户管理 ==========
    @GetMapping("/customers")
    @Operation(summary = "获取所有客户", description = "获取客户列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取客户列表")
    })
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(UserSession session) {
        List<Customer> customers = contactAppService.listCustomers();
        return ResponseEntity.ok(ApiResponse.ok(customers));
    }

    @GetMapping("/customers/{id}")
    @Operation(summary = "获取客户详情", description = "根据ID获取客户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取客户信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "客户不存在")
    })
    public ResponseEntity<ApiResponse<Customer>> getCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            UserSession session) {
        return contactAppService.findCustomer(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.ok(customer)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("客户不存在")));
    }

    @PostMapping("/customers")
    @Operation(summary = "创建客户", description = "创建新客户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建客户")
    })
    public ResponseEntity<ApiResponse<Customer>> createCustomer(
            @RequestBody CreateCustomerRequest request,
            UserSession session) {
        Customer created = contactAppService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/customers/{id}")
    @Operation(summary = "更新客户", description = "更新客户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新客户")
    })
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request,
            UserSession session) {
        Customer updated = contactAppService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "删除客户", description = "删除指定客户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除客户")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            UserSession session) {
        contactAppService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 供应商管理 ==========
    @GetMapping("/suppliers")
    @Operation(summary = "获取所有供应商", description = "获取供应商列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取供应商列表")
    })
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers(UserSession session) {
        List<Supplier> suppliers = contactAppService.listSuppliers();
        return ResponseEntity.ok(ApiResponse.ok(suppliers));
    }

    @GetMapping("/suppliers/{id}")
    @Operation(summary = "获取供应商详情", description = "根据ID获取供应商信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取供应商信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "供应商不存在")
    })
    public ResponseEntity<ApiResponse<Supplier>> getSupplier(
            @Parameter(description = "供应商ID", required = true) @PathVariable Long id,
            UserSession session) {
        return contactAppService.findSupplier(id)
                .map(supplier -> ResponseEntity.ok(ApiResponse.ok(supplier)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("供应商不存在")));
    }

    @PostMapping("/suppliers")
    @Operation(summary = "创建供应商", description = "创建新供应商")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建供应商")
    })
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(
            @RequestBody CreateSupplierRequest request,
            UserSession session) {
        Supplier created = contactAppService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/suppliers/{id}")
    @Operation(summary = "更新供应商", description = "更新供应商信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新供应商")
    })
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(
            @Parameter(description = "供应商ID", required = true) @PathVariable Long id,
            @RequestBody UpdateSupplierRequest request,
            UserSession session) {
        Supplier updated = contactAppService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/suppliers/{id}")
    @Operation(summary = "删除供应商", description = "删除指定供应商")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除供应商")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "供应商ID", required = true) @PathVariable Long id,
            UserSession session) {
        contactAppService.deleteSupplier(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

