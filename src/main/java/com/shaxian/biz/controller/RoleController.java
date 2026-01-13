package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.role.RoleAppService;
import com.shaxian.biz.dto.role.request.CreateRoleRequest;
import com.shaxian.biz.dto.role.request.UpdateRoleRequest;
import com.shaxian.biz.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz/api/roles")
@Tag(name = "角色管理", description = "角色信息管理接口")
public class RoleController {

    private final RoleAppService roleAppService;

    public RoleController(RoleAppService roleAppService) {
        this.roleAppService = roleAppService;
    }

    @GetMapping
    @Operation(summary = "获取所有角色", description = "获取角色列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取角色列表")
    })
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleAppService.listRoles();
        return ResponseEntity.ok(ApiResponse.ok(roles));
    }

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建角色"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody CreateRoleRequest request) {
        try {
            Role role = roleAppService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新角色"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @RequestBody UpdateRoleRequest request) {
        try {
            Role role = roleAppService.updateRole(id, request);
            return ResponseEntity.ok(ApiResponse.ok(role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除指定角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除角色"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id) {
        try {
            roleAppService.deleteRole(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
