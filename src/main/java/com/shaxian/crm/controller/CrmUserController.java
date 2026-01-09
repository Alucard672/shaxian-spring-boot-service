package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.appservice.CrmUserAppService;
import com.shaxian.crm.dto.request.CreateCrmUserRequest;
import com.shaxian.crm.dto.request.CrmUserQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmUserRequest;
import com.shaxian.crm.entity.CrmUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/users")
@Tag(name = "CRM用户管理", description = "CRM系统用户管理接口")
public class CrmUserController {

    private final CrmUserAppService crmUserAppService;

    public CrmUserController(CrmUserAppService crmUserAppService) {
        this.crmUserAppService = crmUserAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询CRM用户列表", description = "分页查询CRM用户，支持按状态、手机号、姓名、邮箱等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户列表")
    })
    public ResponseEntity<ApiResponse<PageResult<CrmUserInfo>>> queryUsers(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmUserQueryRequest request,
            UserSession session) {
        if (request == null) {
            request = new CrmUserQueryRequest();
        }
        PageResult<CrmUserInfo> result = crmUserAppService.queryUsers(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取CRM用户详情", description = "根据ID获取CRM用户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<CrmUserInfo>> getUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            UserSession session) {
        return crmUserAppService.findUser(id)
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("用户不存在")));
    }

    @PostMapping
    @Operation(summary = "创建CRM用户", description = "创建新CRM用户，密码自动设置为手机号后六位")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建用户")
    })
    public ResponseEntity<ApiResponse<CrmUserInfo>> createUser(
            @Valid @RequestBody CreateCrmUserRequest request,
            UserSession session) {
        CrmUserInfo created = crmUserAppService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新CRM用户", description = "更新CRM用户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新用户")
    })
    public ResponseEntity<ApiResponse<CrmUserInfo>> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCrmUserRequest request,
            UserSession session) {
        CrmUserInfo updated = crmUserAppService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新CRM用户状态", description = "启用或停用CRM用户，停用用户将无法登录系统")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新用户状态")
    })
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态", required = true) @RequestParam String status,
            UserSession session) {
        try {
            CrmUserInfo.UserStatus statusEnum = CrmUserInfo.UserStatus.valueOf(status.toUpperCase());
            crmUserAppService.updateUserStatus(id, statusEnum);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("无效的状态值，只能是ACTIVE或INACTIVE"));
        }
    }
}

