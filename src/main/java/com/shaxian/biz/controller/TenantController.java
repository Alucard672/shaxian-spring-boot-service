package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.appservice.tenant.TenantAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.dto.tenant.request.TenantQueryRequest;
import com.shaxian.biz.dto.tenant.request.UpdateTenantRequest;
import com.shaxian.biz.entity.Tenant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "租户管理", description = "租户信息管理接口")
public class TenantController {

    private final TenantAppService tenantAppService;

    public TenantController(TenantAppService tenantAppService) {
        this.tenantAppService = tenantAppService;
    }

    @PostMapping
    @Operation(summary = "创建租户", description = "创建新租户，自动生成租户代码并设置默认有效期")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建租户"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<ApiResponse<Tenant>> createTenant(
            @Valid @RequestBody CreateTenantRequest request,
            UserSession session) {
        try {
            if (session == null || session.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.fail("需要登录才能创建租户"));
            }
            Tenant tenant = tenantAppService.createTenant(request, session.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(tenant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/query")
    @Operation(summary = "查询租户列表", description = "分页查询租户，支持按名称、代码、状态等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取租户列表")
    })
    public ResponseEntity<ApiResponse<PageResult<Tenant>>> queryTenants(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) TenantQueryRequest request,
            UserSession session) {
        if (request == null) {
            request = new TenantQueryRequest();
        }
        PageResult<Tenant> result = tenantAppService.queryTenants(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取租户详情", description = "根据ID获取租户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取租户信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "租户不存在")
    })
    public ResponseEntity<ApiResponse<Tenant>> getTenant(
            @Parameter(description = "租户ID", required = true) @PathVariable Long id,
            UserSession session) {
        return tenantAppService.getTenant(id)
                .map(tenant -> ResponseEntity.ok(ApiResponse.ok(tenant)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("租户不存在")));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户", description = "更新租户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新租户")
    })
    public ResponseEntity<ApiResponse<Tenant>> updateTenant(
            @Parameter(description = "租户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request,
            UserSession session) {
        Tenant updated = tenantAppService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }
}

