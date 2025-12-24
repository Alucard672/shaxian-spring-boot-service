package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.employee.EmployeeAppService;
import com.shaxian.biz.dto.employee.request.CreateEmployeeRequest;
import com.shaxian.biz.dto.employee.request.UpdateEmployeeRequest;
import com.shaxian.biz.entity.Employee;
import com.shaxian.biz.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "员工管理", description = "员工信息管理接口")
public class EmployeeController {

    private final EmployeeAppService employeeAppService;

    public EmployeeController(EmployeeAppService employeeAppService) {
        this.employeeAppService = employeeAppService;
    }

    @GetMapping
    @Operation(summary = "获取所有员工", description = "获取员工列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取员工列表")
    })
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeAppService.listEmployees();
        return ResponseEntity.ok(ApiResponse.ok(employees));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取员工详情", description = "根据ID获取员工信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取员工信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "员工不存在")
    })
    public ResponseEntity<ApiResponse<Employee>> getEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable Long id) {
        return employeeAppService.findEmployee(id)
                .map(employee -> ResponseEntity.ok(ApiResponse.ok(employee)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("员工不存在")));
    }

    @PostMapping
    @Operation(summary = "创建员工", description = "创建新员工")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建员工")
    })
    public ResponseEntity<ApiResponse<Employee>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        Employee created = employeeAppService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新员工", description = "更新员工信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新员工"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "员工不存在")
    })
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {
        try {
            Employee updated = employeeAppService.updateEmployee(id, request);
            return ResponseEntity.ok(ApiResponse.ok(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工", description = "删除指定员工")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除员工"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "员工不存在")
    })
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable Long id) {
        try {
            employeeAppService.deleteEmployee(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/{employeeId}/authorize-login")
    @Operation(summary = "授权员工登录", description = "将员工数据同步到用户表并建立租户关联")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "授权成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "授权失败")
    })
    public ResponseEntity<ApiResponse<User>> authorizeEmployeeLogin(
            @Parameter(description = "员工ID", required = true) @PathVariable Long employeeId,
            @RequestBody Map<String, Long> request) {
        try {
            Long tenantId = request.get("tenantId");
            if (tenantId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.fail("tenantId不能为空"));
            }
            User user = employeeAppService.authorizeEmployeeLogin(employeeId, tenantId);
            return ResponseEntity.ok(ApiResponse.ok("授权成功", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
