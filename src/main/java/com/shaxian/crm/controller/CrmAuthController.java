package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.appservice.CrmAuthAppService;
import com.shaxian.crm.dto.request.CrmLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/api/auth")
@Tag(name = "CRM认证", description = "CRM用户认证接口")
public class CrmAuthController {

    private final CrmAuthAppService crmAuthAppService;

    public CrmAuthController(CrmAuthAppService crmAuthAppService) {
        this.crmAuthAppService = crmAuthAppService;
    }

    @PostMapping("/login")
    @Operation(summary = "CRM用户登录", description = "通过手机号和密码登录CRM系统")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public ResponseEntity<ApiResponse<CrmUserSession>> login(@Valid @RequestBody CrmLoginRequest request) {
        CrmUserSession crmUserSession = crmAuthAppService.login(request.getPhone(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok("登录成功", crmUserSession));
    }

    @PostMapping("/logout")
    @Operation(summary = "CRM用户登出", description = "退出登录，需要传递 sessionId")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登出成功")
    })
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(value = "sessionId", required = false) String sessionIdParam) {
        String sessionId = sessionIdHeader != null ? sessionIdHeader : sessionIdParam;
        crmAuthAppService.logout(sessionId);
        return ResponseEntity.ok(ApiResponse.ok("登出成功", null));
    }

    @GetMapping("/session")
    @Operation(summary = "加载CRM用户会话", description = "通过sessionId加载会话信息，用于页面刷新时恢复会话")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "加载成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "会话不存在或已过期")
    })
    public ResponseEntity<ApiResponse<CrmUserSession>> loadSession(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("sessionId不能为空"));
        }

        try {
            CrmUserSession crmUserSession = crmAuthAppService.loadSession(sessionId);
            return ResponseEntity.ok(ApiResponse.ok("加载成功", crmUserSession));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}

