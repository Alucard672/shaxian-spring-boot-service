package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.crm.appservice.CrmTenantAppService;
import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.dto.request.CrmTenantQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/api/tenants")
@Tag(name = "CRM租户管理", description = "CRM系统租户管理接口")
public class CrmTenantController {

    private final CrmTenantAppService crmTenantAppService;

    public CrmTenantController(CrmTenantAppService crmTenantAppService) {
        this.crmTenantAppService = crmTenantAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询租户列表", description = "分页查询租户，支持按名称、代码、状态等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取租户列表")
    })
    public ResponseEntity<ApiResponse<PageResult<Tenant>>> queryTenants(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmTenantQueryRequest request,
            CrmUserSession session) {
        if (request == null) {
            request = new CrmTenantQueryRequest();
        }
        PageResult<Tenant> result = crmTenantAppService.queryTenants(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "启用租户", description = "启用指定租户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功启用租户")
    })
    public ResponseEntity<ApiResponse<Tenant>> activateTenant(
            @Parameter(description = "租户ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        Tenant tenant = crmTenantAppService.activateTenant(id);
        return ResponseEntity.ok(ApiResponse.ok(tenant));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "停用租户", description = "停用指定租户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功停用租户")
    })
    public ResponseEntity<ApiResponse<Tenant>> deactivateTenant(
            @Parameter(description = "租户ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        Tenant tenant = crmTenantAppService.deactivateTenant(id);
        return ResponseEntity.ok(ApiResponse.ok(tenant));
    }
}
