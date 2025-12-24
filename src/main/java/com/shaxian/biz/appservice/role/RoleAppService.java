package com.shaxian.biz.appservice.role;

import com.shaxian.biz.dto.role.request.CreateRoleRequest;
import com.shaxian.biz.dto.role.request.UpdateRoleRequest;
import com.shaxian.biz.entity.Role;
import com.shaxian.biz.service.settings.RoleService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleAppService {

    private final RoleService roleService;

    public RoleAppService(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<Role> listRoles() {
        return roleService.getAll();
    }

    public Role createRole(CreateRoleRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", request.getName());
        map.put("description", request.getDescription());
        map.put("permissions", request.getPermissions());
        return roleService.create(map);
    }

    public Role updateRole(Long id, UpdateRoleRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (request.getName() != null) map.put("name", request.getName());
        if (request.getDescription() != null) map.put("description", request.getDescription());
        if (request.getPermissions() != null) map.put("permissions", request.getPermissions());
        return roleService.update(id, map);
    }

    public void deleteRole(Long id) {
        roleService.delete(id);
    }
}
