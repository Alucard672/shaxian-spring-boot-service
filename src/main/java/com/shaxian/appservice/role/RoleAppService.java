package com.shaxian.appservice.role;

import com.shaxian.entity.Role;
import com.shaxian.service.settings.RoleService;
import org.springframework.stereotype.Service;

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

    public Role createRole(Map<String, Object> request) throws Exception {
        return roleService.create(request);
    }

    public Role updateRole(Long id, Map<String, Object> request) throws Exception {
        return roleService.update(id, request);
    }

    public void deleteRole(Long id) {
        roleService.delete(id);
    }
}
