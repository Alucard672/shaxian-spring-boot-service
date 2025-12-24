package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.auth.AuthAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.auth.request.RegisterRequest;
import com.shaxian.biz.entity.UserTenant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证授权", description = "用户登录、登出接口")
public class AuthController {

    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过手机号和密码注册，可选择传递租户代码自动关联租户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "手机号已存在或租户代码不存在")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Map<String, Object> userInfo = authAppService.register(
                registerRequest.getPhone(),
                registerRequest.getPassword(),
                registerRequest.getTenantCode()
        );
        return ResponseEntity.ok(ApiResponse.ok("注册成功", userInfo));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过手机号和密码登录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public ResponseEntity<ApiResponse<UserSession>> login(@RequestBody Map<String, String> loginRequest) {
        String phone = loginRequest.get("phone");
        String password = loginRequest.get("password");

        UserSession userSession = authAppService.login(phone, password);
        return ResponseEntity.ok(ApiResponse.ok("登录成功", userSession));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，需要传递 sessionId")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登出成功")
    })
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
                                                     @RequestParam(value = "sessionId", required = false) String sessionIdParam) {
        String sessionId = sessionIdHeader != null ? sessionIdHeader : sessionIdParam;
        authAppService.logout(sessionId);
        return ResponseEntity.ok(ApiResponse.ok("登出成功", null));
    }

    @PostMapping("/switch-tenant")
    @Operation(summary = "切换租户", description = "切换到用户关联的另一个租户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "切换成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户未关联该租户")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> switchTenant(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(value = "sessionId", required = false) String sessionIdParam,
            @RequestBody Map<String, Long> request) {
        String sessionId = sessionIdHeader != null ? sessionIdHeader : sessionIdParam;
        Long tenantId = request.get("tenantId");
        Map<String, Object> userInfo = authAppService.switchTenant(sessionId, tenantId);
        return ResponseEntity.ok(ApiResponse.ok("切换租户成功", userInfo));
    }

    @GetMapping("/user-tenants")
    @Operation(summary = "获取用户租户列表", description = "获取当前用户关联的所有租户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取租户列表")
    })
    public ResponseEntity<ApiResponse<List<UserTenant>>> getUserTenants(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(value = "sessionId", required = false) String sessionIdParam) {
        String sessionId = sessionIdHeader != null ? sessionIdHeader : sessionIdParam;
        List<UserTenant> userTenants = authAppService.getUserTenants(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(userTenants));
    }
}

