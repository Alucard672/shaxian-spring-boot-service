package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.admin.AdminDashboardAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.admin.response.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/biz/api/admin/dashboard")
@Tag(name = "[管理端] Dashboard", description = "首页统计 + 即将到期 + 已到期租户列表")
public class AdminDashboardController {

    private final AdminDashboardAppService adminDashboardAppService;

    public AdminDashboardController(AdminDashboardAppService adminDashboardAppService) {
        this.adminDashboardAppService = adminDashboardAppService;
    }

    @GetMapping
    @Operation(summary = "Dashboard 数据")
    public ResponseEntity<ApiResponse<DashboardVO>> get(UserSession session) {
        return ResponseEntity.ok(ApiResponse.ok(adminDashboardAppService.getDashboard()));
    }
}
