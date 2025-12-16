package com.shaxian.service.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaxian.entity.Role;
import com.shaxian.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public RoleService(RoleRepository roleRepository, ObjectMapper objectMapper) {
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role create(Map<String, Object> request) throws JsonProcessingException {
        Role role = new Role();
        role.setName((String) request.get("name"));
        if (request.containsKey("description")) {
            role.setDescription((String) request.get("description"));
        }
        if (request.containsKey("permissions")) {
            role.setPermissions(objectMapper.writeValueAsString(request.get("permissions")));
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Long id, Map<String, Object> request) throws JsonProcessingException {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));

        Role role = new Role();
        role.setId(id);
        role.setName((String) request.get("name"));
        if (request.containsKey("description")) {
            role.setDescription((String) request.get("description"));
        }
        if (request.containsKey("permissions")) {
            role.setPermissions(objectMapper.writeValueAsString(request.get("permissions")));
        }
        role.setCreatedAt(existing.getCreatedAt());
        return roleRepository.save(role);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("角色不存在");
        }
        roleRepository.deleteById(id);
    }
}
