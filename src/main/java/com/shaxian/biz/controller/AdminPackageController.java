package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.admin.AdminPackageAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.admin.request.UpdatePackageRequest;
import com.shaxian.biz.dto.admin.response.PackageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz/api/admin/packages")
@Tag(name = "[管理端] 套餐管理", description = "套餐只读（本期固定 1 条'标准版'）")
public class AdminPackageController {

    private final AdminPackageAppService adminPackageAppService;

    public AdminPackageController(AdminPackageAppService adminPackageAppService) {
        this.adminPackageAppService = adminPackageAppService;
    }

    @GetMapping
    @Operation(summary = "套餐列表")
    public ResponseEntity<ApiResponse<List<PackageVO>>> list(UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok(adminPackageAppService.listAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "套餐详情")
    public ResponseEntity<ApiResponse<PackageVO>> detail(@PathVariable Long id, UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok(adminPackageAppService.get(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新套餐", description = "可修改名称 / 并发上限 / 年单价 / 状态。改并发上限对现有 session 不立即生效，下次登录按新上限触发顶号")
    public ResponseEntity<ApiResponse<PackageVO>> update(@PathVariable Long id,
                                                        @Valid @RequestBody UpdatePackageRequest request,
                                                        UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok("更新成功", adminPackageAppService.update(id, request)));
    }
}
