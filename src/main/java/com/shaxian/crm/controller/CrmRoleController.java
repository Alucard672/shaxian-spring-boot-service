package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.appservice.CrmRoleAppService;
import com.shaxian.crm.dto.request.CreateCrmRoleRequest;
import com.shaxian.crm.dto.request.CrmRoleQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmRoleRequest;
import com.shaxian.crm.entity.CrmRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/api/roles")
@Tag(name = "CRM角色管理", description = "CRM系统角色管理接口")
public class CrmRoleController {

    private final CrmRoleAppService crmRoleAppService;

    public CrmRoleController(CrmRoleAppService crmRoleAppService) {
        this.crmRoleAppService = crmRoleAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询CRM角色列表", description = "分页查询CRM角色，支持按名称、代码、状态等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取角色列表")
    })
    public ResponseEntity<ApiResponse<PageResult<CrmRole>>> queryRoles(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmRoleQueryRequest request,
            CrmUserSession session) {
        if (request == null) {
            request = new CrmRoleQueryRequest();
        }
        PageResult<CrmRole> result = crmRoleAppService.queryRoles(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取CRM角色详情", description = "根据ID获取CRM角色信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取角色信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在")
    })
    public ResponseEntity<ApiResponse<CrmRole>> getRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        return crmRoleAppService.findRole(id)
                .map(role -> ResponseEntity.ok(ApiResponse.ok(role)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("角色不存在")));
    }

    @PostMapping
    @Operation(summary = "创建CRM角色", description = "创建新CRM角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建角色")
    })
    public ResponseEntity<ApiResponse<CrmRole>> createRole(
            @Valid @RequestBody CreateCrmRoleRequest request,
            CrmUserSession session) {
        CrmRole created = crmRoleAppService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新CRM角色", description = "更新CRM角色信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新角色")
    })
    public ResponseEntity<ApiResponse<CrmRole>> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCrmRoleRequest request,
            CrmUserSession session) {
        CrmRole updated = crmRoleAppService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新CRM角色状态", description = "启用或停用CRM角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新角色状态")
    })
    public ResponseEntity<ApiResponse<Void>> updateRoleStatus(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态", required = true) @RequestParam String status,
            CrmUserSession session) {
        try {
            CrmRole.RoleStatus statusEnum = CrmRole.RoleStatus.valueOf(status.toUpperCase());
            crmRoleAppService.updateRoleStatus(id, statusEnum);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("无效的状态值，只能是ACTIVE或INACTIVE"));
        }
    }
}
