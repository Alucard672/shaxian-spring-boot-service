package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.system.SystemParamsAppService;
import com.shaxian.entity.SystemParams;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system-params")
@Tag(name = "系统参数", description = "系统参数管理接口")
public class SystemParamsController {

    private final SystemParamsAppService systemParamsAppService;

    public SystemParamsController(SystemParamsAppService systemParamsAppService) {
        this.systemParamsAppService = systemParamsAppService;
    }

    @GetMapping
    @Operation(summary = "获取系统参数", description = "获取当前系统参数配置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取系统参数")
    })
    public ResponseEntity<ApiResponse<SystemParams>> getSystemParams() {
        SystemParams params = systemParamsAppService.getSystemParams();
        return ResponseEntity.ok(ApiResponse.ok(params));
    }

    @PutMapping
    @Operation(summary = "更新系统参数", description = "更新系统参数配置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新系统参数")
    })
    public ResponseEntity<ApiResponse<SystemParams>> updateSystemParams(@RequestBody Map<String, Object> request) {
        SystemParams params = systemParamsAppService.updateSystemParams(request);
        return ResponseEntity.ok(ApiResponse.ok(params));
    }
}
