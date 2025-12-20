package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.tenant.TenantAppService;
import com.shaxian.auth.UserSession;
import com.shaxian.dto.tenant.request.CreateTenantRequest;
import com.shaxian.entity.Tenant;
import io.swagger.v3.oas.annotations.Operation;
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
}

