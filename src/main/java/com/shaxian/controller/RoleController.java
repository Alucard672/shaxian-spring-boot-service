package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.role.RoleAppService;
import com.shaxian.dto.role.request.CreateRoleRequest;
import com.shaxian.dto.role.request.UpdateRoleRequest;
import com.shaxian.entity.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleAppService roleAppService;

    public RoleController(RoleAppService roleAppService) {
        this.roleAppService = roleAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleAppService.listRoles();
        return ResponseEntity.ok(ApiResponse.ok(roles));
    }

    @PostMapping
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
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
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
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            roleAppService.deleteRole(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
