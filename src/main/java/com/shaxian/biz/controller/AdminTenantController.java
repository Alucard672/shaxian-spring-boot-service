package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.appservice.admin.AdminTenantAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.admin.request.RenewTenantRequest;
import com.shaxian.biz.dto.admin.request.TenantAdminQueryRequest;
import com.shaxian.biz.dto.admin.response.SubscriptionVO;
import com.shaxian.biz.dto.admin.response.TenantDetailVO;
import com.shaxian.biz.dto.admin.response.TenantVO;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.dto.tenant.request.UpdateTenantRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biz/api/admin/tenants")
@Tag(name = "[管理端] 租户管理", description = "平台超管管理租户，含套餐 / 续费 / 状态")
public class AdminTenantController {

    private final AdminTenantAppService adminTenantAppService;

    public AdminTenantController(AdminTenantAppService adminTenantAppService) {
        this.adminTenantAppService = adminTenantAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "租户分页查询", description = "支持名称 / 代码 / 状态 / 套餐 / 业务员 / X 天内到期 过滤")
    public ResponseEntity<ApiResponse<PageResult<TenantVO>>> query(
            @Parameter(description = "页码（从1开始）", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) TenantAdminQueryRequest request,
            UserSession session) {
        if (request == null) request = new TenantAdminQueryRequest();
        return ResponseEntity.ok(ApiResponse.ok(adminTenantAppService.listTenants(request, pageNo, pageSize)));
    }

    @PostMapping
    @Operation(summary = "新建租户", description = "必填：name + expiresAt；packageId 缺省取'标准版'")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ResponseEntity<ApiResponse<TenantVO>> create(@Valid @RequestBody CreateTenantRequest request,
                                                       UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok("创建成功",
                adminTenantAppService.createTenant(request, session.getUserId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "租户详情", description = "含订阅记录列表 + 当前活跃 session（脱敏）")
    public ResponseEntity<ApiResponse<TenantDetailVO>> detail(@PathVariable Long id, UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok(adminTenantAppService.getTenantDetail(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户", description = "status 或 expiresAt 变更后会主动失效该租户所有 session")
    public ResponseEntity<ApiResponse<TenantVO>> update(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateTenantRequest request,
                                                       UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok("更新成功",
                adminTenantAppService.updateTenant(id, request)));
    }

    @PostMapping("/{id}/renew")
    @Operation(summary = "租户续费", description = "录入金额 + 延至日期，新增订阅记录并更新到期；提交后该租户所有 session 被失效")
    public ResponseEntity<ApiResponse<SubscriptionVO>> renew(@PathVariable Long id,
                                                            @Valid @RequestBody RenewTenantRequest request,
                                                            UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok("续费成功",
                adminTenantAppService.renew(id, request, session.getUserId())));
    }
}
