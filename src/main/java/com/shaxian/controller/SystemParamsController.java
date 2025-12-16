package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.system.SystemParamsAppService;
import com.shaxian.entity.SystemParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system-params")
public class SystemParamsController {

    private final SystemParamsAppService systemParamsAppService;

    public SystemParamsController(SystemParamsAppService systemParamsAppService) {
        this.systemParamsAppService = systemParamsAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SystemParams>> getSystemParams() {
        SystemParams params = systemParamsAppService.getSystemParams();
        return ResponseEntity.ok(ApiResponse.ok(params));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SystemParams>> updateSystemParams(@RequestBody Map<String, Object> request) {
        SystemParams params = systemParamsAppService.updateSystemParams(request);
        return ResponseEntity.ok(ApiResponse.ok(params));
    }
}
