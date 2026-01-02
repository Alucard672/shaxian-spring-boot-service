package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.appservice.CrmAuthAppService;
import com.shaxian.crm.dto.request.CrmLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/auth")
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
    public ResponseEntity<ApiResponse<UserSession>> login(@Valid @RequestBody CrmLoginRequest request) {
        UserSession userSession = crmAuthAppService.login(request.getPhone(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok("登录成功", userSession));
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
}

