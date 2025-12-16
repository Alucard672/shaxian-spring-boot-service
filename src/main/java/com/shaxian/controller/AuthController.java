package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.auth.AuthAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> loginRequest) {
        String phone = loginRequest.get("phone");
        String password = loginRequest.get("password");

        Map<String, Object> userInfo = authAppService.login(phone, password);
        return ResponseEntity.ok(ApiResponse.ok("登录成功", userInfo));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("登出成功", null));
    }
}

